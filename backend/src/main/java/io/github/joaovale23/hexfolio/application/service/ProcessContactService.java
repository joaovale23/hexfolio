package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.ProcessContact;
import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.application.port.out.Notifier;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent.Step;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Processa o que sai da fila. Se a notificação falhar, a exceção sobe para o adaptador da fila,
 * que não confirma a mensagem: o SQS entrega de novo e, depois das tentativas, manda para a DLQ.
 */
@UseCase("Notifica a chegada de um contato; é idempotente e desiste depois das tentativas")
public class ProcessContactService implements ProcessContact {

    private static final Logger LOG = System.getLogger(ProcessContactService.class.getName());

    private final ContactRepository repository;
    private final Notifier notifier;
    private final Clock clock;

    public ProcessContactService(ContactRepository repository, Notifier notifier, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.notifier = Objects.requireNonNull(notifier);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public void process(UUID contactId) {
        Optional<ContactMessage> found = pending(contactId);
        if (found.isEmpty()) {
            return;
        }
        ContactMessage message = found.get();

        Instant start = clock.instant();
        message.markProcessing();
        repository.save(message);
        repository.append(event(contactId, Step.PROCESSING, start, "ProcessContact"));

        Instant notifyStart = clock.instant();
        try {
            notifier.notify(message);
        } catch (RuntimeException e) {
            repository.append(event(contactId, Step.ATTEMPT_FAILED, notifyStart, "Notifier"));
            throw e;
        }
        message.markNotified();
        repository.save(message);
        repository.append(event(contactId, Step.NOTIFIED, notifyStart, "Notifier"));
    }

    @Override
    public void giveUp(UUID contactId) {
        pending(contactId).ifPresent(message -> {
            Instant start = clock.instant();
            message.markFailed();
            repository.save(message);
            repository.append(event(contactId, Step.FAILED, start, "ContactQueue"));
            LOG.log(Level.WARNING, "Contato {0} foi para a DLQ depois das tentativas", contactId);
        });
    }

    /** A mensagem, se existir e ainda não tiver um desfecho. É isso que garante a idempotência. */
    private Optional<ContactMessage> pending(UUID contactId) {
        Optional<ContactMessage> found = repository.findById(contactId);
        if (found.isEmpty()) {
            LOG.log(Level.WARNING, "Contato {0} não encontrado; mensagem descartada", contactId);
            return Optional.empty();
        }
        if (found.get().status().isFinal()) {
            LOG.log(Level.INFO, "Contato {0} já está {1}; nada a fazer", contactId, found.get().status());
            return Optional.empty();
        }
        return found;
    }

    private TraceEvent event(UUID id, Step step, Instant start, String component) {
        return new TraceEvent(id, step, start, Duration.between(start, clock.instant()).toMillis(), component);
    }
}
