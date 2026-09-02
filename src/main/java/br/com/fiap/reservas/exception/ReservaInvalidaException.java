package br.com.fiap.reservas.exception;

/**
 * Lançada quando uma reserva de equipamentos viola alguma das regras de
 * negócio do sistema (antecedência mínima, conflito de horário/sala,
 * conflito de equipamento, horário inválido ou equipamento inativo).
 *
 * O sistema deve sempre informar claramente, através da mensagem desta
 * exceção, o motivo da rejeição da reserva.
 */
public class ReservaInvalidaException extends RuntimeException {

    public ReservaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
