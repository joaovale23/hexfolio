package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.application.port.in.TraceContact;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Transmite as etapas de uma mensagem por Server-Sent Events, para o modo dev acender o hexágono.
 * Cada conexão roda numa virtual thread que consulta o caso de uso até a mensagem chegar ao fim.
 */
@RestController
@RequestMapping("/api/contact")
@InboundAdapter("GET /api/contact/{id}/trace: transmite ao vivo (SSE) as etapas da mensagem")
class ContactTraceController {

    private final TraceContact traceContact;
    private final Duration maxDuration;
    private final Duration pollInterval;

    ContactTraceController(TraceContact traceContact,
                           @Value("${hexfolio.trace.max-duration:30s}") Duration maxDuration,
                           @Value("${hexfolio.trace.poll-interval:150ms}") Duration pollInterval) {
        this.traceContact = traceContact;
        this.maxDuration = maxDuration;
        this.pollInterval = pollInterval;
    }

    record TraceEventResponse(String step, Instant at, long tookMs, String component) {
        static TraceEventResponse from(TraceEvent e) {
            return new TraceEventResponse(e.step().name(), e.at(), e.tookMs(), e.component());
        }
    }

    @GetMapping(path = "/{id}/trace", produces = "text/event-stream")
    ResponseEntity<SseEmitter> trace(@PathVariable UUID id) {
        if (traceContact.trace(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SseEmitter emitter = new SseEmitter(maxDuration.plusSeconds(5).toMillis());
        Thread.ofVirtual().name("trace-" + id).start(() -> stream(id, emitter));
        return ResponseEntity.ok(emitter);
    }

    private void stream(UUID id, SseEmitter emitter) {
        Instant deadline = Instant.now().plus(maxDuration);
        int sent = 0;
        try {
            while (Instant.now().isBefore(deadline)) {
                List<TraceEvent> events = traceContact.trace(id).orElse(List.of());
                for (TraceEvent event : events.subList(sent, events.size())) {
                    emitter.send(SseEmitter.event().name("step").data(TraceEventResponse.from(event)));
                    sent++;
                    if (event.step().isFinal()) {
                        emitter.complete();
                        return;
                    }
                }
                Thread.sleep(pollInterval);
            }
            emitter.send(SseEmitter.event().name("timeout").data("{}"));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e); // o visitante fechou a página
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            emitter.complete();
        }
    }
}
