package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.out.Notifier;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.ContactStatus;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ProcessContactServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-20T15:00:00Z");

    private final InMemoryContactRepository repository = new InMemoryContactRepository();
    private final Notifier notifier = mock(Notifier.class);
    private ProcessContactService service;
    private UUID id;

    @BeforeEach
    void setUp() {
        service = new ProcessContactService(repository, notifier, Clock.fixed(NOW, ZoneOffset.UTC));
        ContactMessage message = ContactMessage.receive(UUID.randomUUID(), "Ana", "ana@empresa.com", "Olá, João! Vamos conversar?", NOW);
        message.markEnqueued();
        repository.save(message);
        id = message.id();
    }

    @Test
    void notificaEMarcaComoNotificada() {
        service.process(id);

        verify(notifier).notify(repository.messages.get(id));
        assertThat(repository.messages.get(id).status()).isEqualTo(ContactStatus.NOTIFIED);
        assertThat(repository.stepsOf(id)).containsExactly(Step.PROCESSING, Step.NOTIFIED);
    }

    @Test
    void eIdempotenteQuandoAMesmaMensagemChegaDuasVezes() {
        service.process(id);
        service.process(id);

        verify(notifier, times(1)).notify(any());
        assertThat(repository.stepsOf(id)).containsExactly(Step.PROCESSING, Step.NOTIFIED);
    }

    @Test
    void propagaAFalhaParaAFilaTentarDeNovo() {
        doThrow(new IllegalStateException("SMTP fora do ar")).when(notifier).notify(any());

        assertThatThrownBy(() -> service.process(id)).hasMessage("SMTP fora do ar");

        assertThat(repository.messages.get(id).status()).isEqualTo(ContactStatus.PROCESSING);
        assertThat(repository.stepsOf(id)).containsExactly(Step.PROCESSING, Step.ATTEMPT_FAILED);
    }

    @Test
    void desisteDepoisDasTentativas() {
        doThrow(new IllegalStateException("SMTP fora do ar")).when(notifier).notify(any());
        assertThatThrownBy(() -> service.process(id)).isInstanceOf(IllegalStateException.class);

        service.giveUp(id);

        assertThat(repository.messages.get(id).status()).isEqualTo(ContactStatus.FAILED);
        assertThat(repository.stepsOf(id)).endsWith(Step.FAILED);
    }

    @Test
    void ignoraMensagemDesconhecida() {
        service.process(UUID.randomUUID());
        service.giveUp(UUID.randomUUID());

        verifyNoInteractions(notifier);
    }
}
