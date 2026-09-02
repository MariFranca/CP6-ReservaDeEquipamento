package br.com.fiap.reservas.dto;

import br.com.fiap.reservas.entity.Equipamento;
import br.com.fiap.reservas.entity.Reserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dados devolvidos ao cliente representando uma reserva já confirmada.
 * Evitamos expor as entidades JPA diretamente para não vazar detalhes
 * internos de mapeamento e não sofrer com referências cíclicas no JSON.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private Long id;
    private String professor;
    private String curso;
    private String sala;
    private LocalDateTime horarioRetirada;
    private LocalDateTime horarioEntrega;
    private List<String> equipamentos;

    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .professor(reserva.getProfessor().getNome())
                .curso(reserva.getCurso().getNome())
                .sala(reserva.getSala().getNumero())
                .horarioRetirada(reserva.getHorarioRetirada())
                .horarioEntrega(reserva.getHorarioEntrega())
                .equipamentos(reserva.getEquipamentos().stream()
                        .map(Equipamento::getIdentificacao)
                        .collect(Collectors.toList()))
                .build();
    }
}
