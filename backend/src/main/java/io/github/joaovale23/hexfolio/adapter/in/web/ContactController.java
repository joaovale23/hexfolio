package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.application.port.in.SubmitContact;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/contact")
@InboundAdapter("POST /api/contact: recebe o formulário e responde 202 sem esperar o e-mail")
class ContactController {

    private final SubmitContact submitContact;
    private final ContactRateLimiter rateLimiter;

    ContactController(SubmitContact submitContact, Clock clock,
                      @Value("${hexfolio.contact.rate-limit-per-hour:5}") int limitPerHour) {
        this.submitContact = submitContact;
        this.rateLimiter = new ContactRateLimiter(limitPerHour, Duration.ofHours(1), clock);
    }

    /** {@code website} é um campo isca: invisível para pessoas, preenchido por robôs. */
    record ContactRequest(String name, String email, String message, String website) {
    }

    record ContactAccepted(UUID id, String traceUrl) {
    }

    @PostMapping
    ResponseEntity<ContactAccepted> submit(@RequestBody ContactRequest request, HttpServletRequest http) {
        if (request.website() != null && !request.website().isBlank()) {
            // Robô: responde como se tivesse dado certo, para ele não tentar de novo.
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ContactAccepted(UUID.randomUUID(), null));
        }
        rateLimiter.tryAcquire(clientIp(http)).ifPresent(wait -> {
            throw new TooManyContactsException(wait);
        });

        UUID id = submitContact.submit(new SubmitContact.Command(request.name(), request.email(), request.message()));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ContactAccepted(id, "/api/contact/" + id + "/trace"));
    }

    /** No Fly.io, o IP real do visitante chega no cabeçalho Fly-Client-IP. */
    private static String clientIp(HttpServletRequest http) {
        String flyIp = http.getHeader("Fly-Client-IP");
        return flyIp != null && !flyIp.isBlank() ? flyIp : http.getRemoteAddr();
    }

    static class TooManyContactsException extends RuntimeException {
        final Duration retryAfter;

        TooManyContactsException(Duration retryAfter) {
            super("Limite de mensagens atingido");
            this.retryAfter = retryAfter;
        }
    }
}
