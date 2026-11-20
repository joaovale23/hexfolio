package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.SubmitContact.Command;
import io.github.joaovale23.hexfolio.application.port.out.ContactQueue;
import io.github.joaovale23.hexfolio.application.port.in.ContactQueueUnavailableException;
import io.github.joaovale23.hexfolio.domain.model.ContactStatus;
import io.github.joaovale23.hexfolio.domain.model.InvalidContactException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SubmitContactServiceTest {

    private static final UUID ID = UUID.fromString("7f1c2d3e-0000-4000-8000-000000000001");
    private static final Command VALID = new Command("Ana", "ana@empresa.com", "Olá, João! Vamos conversar?");

    private final InMemoryContactRepository repository = new InMemoryContactRepository();
    private final ContactQueue queue = mock(ContactQueue.class);
    private SubmitContactService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-20T15:00:00Z"), ZoneOffset.UTC);
        service = new SubmitContactService(repository, queue, clock, () -> ID);
    }

    @Test
    void gravaPublicaNaFilaERegistraAsEtapas() {
        UUID id = service.submit(VALID);

        assertThat(id).isEqualTo(ID);
        assertThat(repository.messages.get(ID).status()).isEqualTo(ContactStatus.ENQUEUED);
        assertThat(repository.stepsOf(ID)).containsExactly(Step.RECEIVED, Step.ENQUEUED);
        assertThat(repository.traceOf(ID)).extracting("component").containsExactly("SubmitContact", "ContactQueue");
        verify(queue).publish(ID);
    }

    @Test
    void naoPublicaMensagemInvalida() {
        assertThatThrownBy(() -> service.submit(new Command("", "x", "")))
                .isInstanceOf(InvalidContactException.class);

        assertThat(repository.messages).isEmpty();
        verifyNoInteractions(queue);
    }

    @Test
    void marcaComoFalhaQuandoAFilaRecusa() {
        doThrow(new IllegalStateException("SQS fora do ar")).when(queue).publish(any());

        assertThatThrownBy(() -> service.submit(VALID)).isInstanceOf(ContactQueueUnavailableException.class);

        assertThat(repository.messages.get(ID).status()).isEqualTo(ContactStatus.FAILED);
        assertThat(repository.stepsOf(ID)).containsExactly(Step.RECEIVED, Step.FAILED);
    }
}
