package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.adapter.in.web.ContactController.TooManyContactsException;
import io.github.joaovale23.hexfolio.application.port.in.ContactQueueUnavailableException;
import io.github.joaovale23.hexfolio.domain.model.InvalidContactException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Converte as exceções em respostas no formato Problem Details (RFC 9457). */
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(InvalidContactException.class)
    ProblemDetail invalidContact(InvalidContactException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Revise os campos destacados.");
        problem.setTitle("Mensagem inválida");
        problem.setProperty("errors", e.fieldErrors());
        return problem;
    }

    @ExceptionHandler(TooManyContactsException.class)
    ResponseEntity<ProblemDetail> tooMany(TooManyContactsException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS,
                "Você já enviou várias mensagens. Tente de novo mais tarde.");
        problem.setTitle("Muitas mensagens");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, e.retryAfter.toSeconds())))
                .body(problem);
    }

    @ExceptionHandler(ContactQueueUnavailableException.class)
    ProblemDetail queueUnavailable() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Não foi possível registrar sua mensagem agora. Tente de novo em alguns minutos.");
        problem.setTitle("Fila indisponível");
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail badParameter(MethodArgumentTypeMismatchException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Parâmetro inválido: " + e.getName());
        problem.setTitle("Requisição inválida");
        return problem;
    }
}
