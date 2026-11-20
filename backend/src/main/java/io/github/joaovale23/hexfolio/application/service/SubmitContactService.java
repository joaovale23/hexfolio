package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.ContactQueueUnavailableException;
import io.github.joaovale23.hexfolio.application.port.in.SubmitContact;
import io.github.joaovale23.hexfolio.application.port.out.ContactQueue;
import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent.Step;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Recebe a mensagem, grava e publica na fila. O visitante recebe a resposta sem esperar o e-mail.
 * O status é gravado como ENQUEUED antes de publicar, para que o consumidor nunca encontre
 * a mensagem num estado anterior ao da fila.
 */
@UseCase("Recebe a mensagem do formulário, grava e publica na fila")
public class SubmitContactService implements SubmitContact {

    private final ContactRepository repository;
    private final ContactQueue queue;
    private final Clock clock;
    private final Supplier<UUID> ids;

    public SubmitContactService(ContactRepository repository, ContactQueue queue, Clock clock, Supplier<UUID> ids) {
        this.repository = Objects.requireNonNull(repository);
        this.queue = Objects.requireNonNull(queue);
        this.clock = Objects.requireNonNull(clock);
        this.ids = Objects.requireNonNull(ids);
    }

    @Override
    public UUID submit(Command command) {
        Instant start = clock.instant();
        ContactMessage message = ContactMessage.receive(ids.get(), command.name(), command.email(), command.message(), start);
        repository.save(message);
        repository.append(event(message.id(), Step.RECEIVED, start, "SubmitContact"));

        Instant enqueueStart = clock.instant();
        message.markEnqueued();
        repository.save(message);
        try {
            queue.publish(message.id());
        } catch (RuntimeException e) {
            message.markFailed();
            repository.save(message);
            repository.append(event(message.id(), Step.FAILED, enqueueStart, "ContactQueue"));
            throw new ContactQueueUnavailableException(e);
        }
        repository.append(event(message.id(), Step.ENQUEUED, enqueueStart, "ContactQueue"));
        return message.id();
    }

    private TraceEvent event(UUID id, Step step, Instant start, String component) {
        long took = Duration.between(start, clock.instant()).toMillis();
        return new TraceEvent(id, step, start, took, component);
    }
}
