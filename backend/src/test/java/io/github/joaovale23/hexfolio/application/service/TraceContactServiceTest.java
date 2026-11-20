package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent.Step;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TraceContactServiceTest {

    private final InMemoryContactRepository repository = new InMemoryContactRepository();
    private final TraceContactService service = new TraceContactService(repository);

    @Test
    void devolveAsEtapasDeUmaMensagemExistente() {
        Instant now = Instant.parse("2026-11-20T15:00:00Z");
        ContactMessage message = ContactMessage.receive(UUID.randomUUID(), "Ana", "ana@empresa.com", "Olá, João! Vamos conversar?", now);
        repository.save(message);
        repository.append(new TraceEvent(message.id(), Step.RECEIVED, now, 3, "SubmitContact"));

        assertThat(service.trace(message.id())).hasValueSatisfying(events ->
                assertThat(events).extracting(TraceEvent::step).containsExactly(Step.RECEIVED));
    }

    @Test
    void ficaVazioParaMensagemDesconhecida() {
        assertThat(service.trace(UUID.randomUUID())).isEmpty();
    }
}
