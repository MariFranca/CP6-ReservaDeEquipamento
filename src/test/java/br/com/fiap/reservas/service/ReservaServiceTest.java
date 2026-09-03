package br.com.fiap.reservas.service;

import br.com.fiap.reservas.dto.ReservaRequestDTO;
import br.com.fiap.reservas.entity.*;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.exception.ReservaInvalidaException;
import br.com.fiap.reservas.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários das regras de negócio de ReservaService. Cada teste
 * cobre exatamente uma regra descrita no enunciado do desafio.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private SalaRepository salaRepository;
    @Mock private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Professor professor;
    private Curso curso;
    private Sala sala;
    private Equipamento datashow;

    @BeforeEach
    void setUp() {
        professor = Professor.builder().id(1L).nome("João da Silva").email("joao@fiap.com").build();
        curso = Curso.builder().id(1L).nome("Engenharia de Software").build();
        sala = Sala.builder().id(1L).numero("204").build();
        datashow = Equipamento.builder().id(1L).identificacao("Datashow 01").tipo("Datashow").ativo(true).build();
    }

    private ReservaRequestDTO montarRequestValido() {
        LocalDateTime retirada = LocalDateTime.now().plusDays(10).withHour(18).withMinute(30);
        LocalDateTime entrega = retirada.plusHours(4);

        return ReservaRequestDTO.builder()
                .professorId(1L)
                .cursoId(1L)
                .salaId(1L)
                .horarioRetirada(retirada)
                .horarioEntrega(entrega)
                .equipamentoIds(List.of(1L))
                .build();
    }

    private void mockarBuscasBasicas() {
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(equipamentoRepository.findById(1L)).thenReturn(Optional.of(datashow));
    }

    @Test
    void deveCriarReserva_quandoTodasAsRegrasForemAtendidas() {
        mockarBuscasBasicas();
        when(reservaRepository.buscarConflitosDeSala(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.buscarConflitosDeEquipamento(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Reserva reserva = reservaService.criar(montarRequestValido());

        assertNotNull(reserva);
        assertEquals(professor, reserva.getProfessor());
        assertEquals(sala, reserva.getSala());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecao_quandoProfessorNaoExiste() {
        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> reservaService.criar(montarRequestValido()));

        verifyNoInteractions(reservaRepository);
    }

    @Test
    void deveLancarExcecao_quandoAntecedenciaForMenorQueSeteDias() {
        mockarBuscasBasicas();

        ReservaRequestDTO dto = montarRequestValido();
        dto.setHorarioRetirada(LocalDateTime.now().plusDays(3).withHour(18));
        dto.setHorarioEntrega(LocalDateTime.now().plusDays(3).withHour(20));

        ReservaInvalidaException excecao = assertThrows(ReservaInvalidaException.class,
                () -> reservaService.criar(dto));

        assertTrue(excecao.getMessage().contains("antecedência"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoRetiradaNaoForAnteriorAEntrega() {
        mockarBuscasBasicas();

        LocalDateTime momento = LocalDateTime.now().plusDays(10).withHour(20);
        ReservaRequestDTO dto = montarRequestValido();
        dto.setHorarioRetirada(momento);
        dto.setHorarioEntrega(momento); // retirada == entrega

        ReservaInvalidaException excecao = assertThrows(ReservaInvalidaException.class,
                () -> reservaService.criar(dto));

        assertTrue(excecao.getMessage().contains("anterior"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoEquipamentoEstiverInativo() {
        datashow.setAtivo(false);
        mockarBuscasBasicas();

        ReservaInvalidaException excecao = assertThrows(ReservaInvalidaException.class,
                () -> reservaService.criar(montarRequestValido()));

        assertTrue(excecao.getMessage().contains("inativos"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoHouverConflitoDeSala() {
        mockarBuscasBasicas();

        Reserva reservaExistente = Reserva.builder()
                .id(99L)
                .sala(sala)
                .horarioRetirada(LocalDateTime.now().plusDays(10).withHour(18))
                .horarioEntrega(LocalDateTime.now().plusDays(10).withHour(20))
                .build();

        when(reservaRepository.buscarConflitosDeSala(anyLong(), any(), any()))
                .thenReturn(List.of(reservaExistente));

        ReservaInvalidaException excecao = assertThrows(ReservaInvalidaException.class,
                () -> reservaService.criar(montarRequestValido()));

        assertTrue(excecao.getMessage().contains("sala"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_quandoHouverConflitoDeEquipamento() {
        mockarBuscasBasicas();
        when(reservaRepository.buscarConflitosDeSala(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        Reserva reservaExistente = Reserva.builder()
                .id(99L)
                .horarioRetirada(LocalDateTime.now().plusDays(10).withHour(18))
                .horarioEntrega(LocalDateTime.now().plusDays(10).withHour(20))
                .build();

        when(reservaRepository.buscarConflitosDeEquipamento(anyLong(), any(), any()))
                .thenReturn(List.of(reservaExistente));

        ReservaInvalidaException excecao = assertThrows(ReservaInvalidaException.class,
                () -> reservaService.criar(montarRequestValido()));

        assertTrue(excecao.getMessage().contains("Datashow 01"));
        verify(reservaRepository, never()).save(any());
    }
}
