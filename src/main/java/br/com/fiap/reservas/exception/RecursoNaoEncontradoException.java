package br.com.fiap.reservas.exception;

/**
 * Lançada quando um recurso (professor, curso, sala, equipamento ou
 * reserva) informado no request não existe na base de dados.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
