package br.com.fiap.reservas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dados recebidos do cliente (Insomnia/front-end) para solicitar uma
 * nova reserva de equipamentos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequestDTO {

    @NotNull(message = "O professor é obrigatório")
    private Long professorId;

    @NotNull(message = "O curso é obrigatório")
    private Long cursoId;

    @NotNull(message = "A sala é obrigatória")
    private Long salaId;

    @NotNull(message = "O horário de retirada é obrigatório")
    private LocalDateTime horarioRetirada;

    @NotNull(message = "O horário de entrega é obrigatório")
    private LocalDateTime horarioEntrega;

    @NotEmpty(message = "É necessário informar ao menos um equipamento")
    private List<Long> equipamentoIds;
}
