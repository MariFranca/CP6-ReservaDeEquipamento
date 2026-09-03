package br.com.fiap.reservas.service;

import br.com.fiap.reservas.dto.ReservaRequestDTO;
import br.com.fiap.reservas.entity.*;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.exception.ReservaInvalidaException;
import br.com.fiap.reservas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private static final long DIAS_MINIMOS_DE_ANTECEDENCIA = 7;

    private final ReservaRepository reservaRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final SalaRepository salaRepository;
    private final EquipamentoRepository equipamentoRepository;

    public ReservaService(ReservaRepository reservaRepository,
                           ProfessorRepository professorRepository,
                           CursoRepository cursoRepository,
                           SalaRepository salaRepository,
                           EquipamentoRepository equipamentoRepository) {
        this.reservaRepository = reservaRepository;
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
        this.salaRepository = salaRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada: " + id));
    }

    public void excluir(Long id) {
        Reserva reserva = buscarPorId(id);
        reservaRepository.delete(reserva);
    }

    @Transactional
    public Reserva criar(ReservaRequestDTO dto) {

        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Professor não encontrado: " + dto.getProfessorId()));

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Curso não encontrado: " + dto.getCursoId()));

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Sala não encontrada: " + dto.getSalaId()));

        List<Equipamento> equipamentos = buscarEquipamentos(dto.getEquipamentoIds());

        validarHorarios(dto.getHorarioRetirada(), dto.getHorarioEntrega());
        validarAntecedenciaMinima(dto.getHorarioRetirada());
        validarEquipamentosAtivos(equipamentos);
        validarConflitoDeSala(sala, dto.getHorarioRetirada(), dto.getHorarioEntrega());
        validarConflitoDeEquipamentos(equipamentos, dto.getHorarioRetirada(), dto.getHorarioEntrega());

        Reserva reserva = Reserva.builder()
                .professor(professor)
                .curso(curso)
                .sala(sala)
                .horarioRetirada(dto.getHorarioRetirada())
                .horarioEntrega(dto.getHorarioEntrega())
                .dataCriacao(LocalDateTime.now())
                .equipamentos(equipamentos)
                .build();

        return reservaRepository.save(reserva);
    }

    private List<Equipamento> buscarEquipamentos(List<Long> equipamentoIds) {
        Set<Long> idsUnicos = new LinkedHashSet<>(equipamentoIds);

        List<Equipamento> equipamentos = idsUnicos.stream()
                .map(id -> equipamentoRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Equipamento não encontrado: " + id)))
                .collect(Collectors.toList());

        if (equipamentos.isEmpty()) {
            throw new ReservaInvalidaException("É necessário informar ao menos um equipamento para a reserva.");
        }

        return equipamentos;
    }

    private void validarHorarios(LocalDateTime retirada, LocalDateTime entrega) {
        if (!retirada.isBefore(entrega)) {
            throw new ReservaInvalidaException(
                    "O horário de retirada deve ser anterior ao horário de entrega.");
        }
    }

    private void validarAntecedenciaMinima(LocalDateTime retirada) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataDaReserva = retirada.toLocalDate();

        long diasDeAntecedencia = ChronoUnit.DAYS.between(hoje, dataDaReserva);

        if (diasDeAntecedencia < DIAS_MINIMOS_DE_ANTECEDENCIA) {
            throw new ReservaInvalidaException(String.format(
                    "A reserva deve ser feita com no mínimo %d dias de antecedência. " +
                            "Data informada: %s (faltam apenas %d dia(s) de antecedência).",
                    DIAS_MINIMOS_DE_ANTECEDENCIA, dataDaReserva,
                    Math.max(diasDeAntecedencia, 0)));
        }
    }

    private void validarEquipamentosAtivos(List<Equipamento> equipamentos) {
        List<String> inativos = equipamentos.stream()
                .filter(equipamento -> !Boolean.TRUE.equals(equipamento.getAtivo()))
                .map(Equipamento::getIdentificacao)
                .collect(Collectors.toList());

        if (!inativos.isEmpty()) {
            throw new ReservaInvalidaException(
                    "Os seguintes equipamentos estão inativos e não podem ser reservados: "
                            + String.join(", ", inativos));
        }
    }

    private void validarConflitoDeSala(Sala sala, LocalDateTime retirada, LocalDateTime entrega) {
        List<Reserva> conflitos = reservaRepository.buscarConflitosDeSala(sala.getId(), retirada, entrega);

        if (!conflitos.isEmpty()) {
            throw new ReservaInvalidaException(String.format(
                    "A sala %s já possui uma reserva no período de %s até %s.",
                    sala.getNumero(),
                    conflitos.get(0).getHorarioRetirada(),
                    conflitos.get(0).getHorarioEntrega()));
        }
    }

    private void validarConflitoDeEquipamentos(List<Equipamento> equipamentos,
                                                LocalDateTime retirada,
                                                LocalDateTime entrega) {
        for (Equipamento equipamento : equipamentos) {
            List<Reserva> conflitos = reservaRepository.buscarConflitosDeEquipamento(
                    equipamento.getId(), retirada, entrega);

            if (!conflitos.isEmpty()) {
                throw new ReservaInvalidaException(String.format(
                        "O equipamento '%s' já está reservado no período de %s até %s.",
                        equipamento.getIdentificacao(),
                        conflitos.get(0).getHorarioRetirada(),
                        conflitos.get(0).getHorarioEntrega()));
            }
        }
    }
}
