package br.com.fiap.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centraliza o tratamento de exceções da API, garantindo que o motivo da
 * rejeição de uma reserva (ou qualquer outro erro) seja sempre informado
 * de forma clara no corpo da resposta JSON.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ReservaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> tratarReservaInvalida(ReservaInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErro(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoErro(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarCamposInvalidos(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .orElse("Dados inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErro(HttpStatus.BAD_REQUEST, mensagem));
    }

    private Map<String, Object> corpoErro(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
