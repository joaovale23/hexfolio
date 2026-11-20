package io.github.joaovale23.hexfolio.adapter.out.persistence;

import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@OutboundAdapter("Grava mensagens e etapas do caminho no PostgreSQL (Spring Data JPA)")
class JpaContactRepository implements ContactRepository {

    private final ContactMessageJpaRepository messages;
    private final ContactTraceJpaRepository traces;
    private final Clock clock;

    JpaContactRepository(ContactMessageJpaRepository messages, ContactTraceJpaRepository traces, Clock clock) {
        this.messages = messages;
        this.traces = traces;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void save(ContactMessage message) {
        ContactMessageEntity entity = messages.findById(message.id()).orElseGet(ContactMessageEntity::new);
        entity.id = message.id();
        entity.name = message.name();
        entity.email = message.email();
        entity.message = message.message();
        entity.status = message.status();
        entity.receivedAt = message.receivedAt();
        entity.updatedAt = clock.instant();
        messages.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactMessage> findById(UUID id) {
        return messages.findById(id)
                .map(e -> ContactMessage.restore(e.id, e.name, e.email, e.message, e.receivedAt, e.status));
    }

    @Override
    @Transactional
    public void append(TraceEvent event) {
        ContactTraceEntity entity = new ContactTraceEntity();
        entity.contactId = event.contactId();
        entity.step = event.step();
        entity.at = event.at();
        entity.tookMs = event.tookMs();
        entity.component = event.component();
        traces.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraceEvent> traceOf(UUID contactId) {
        return traces.findByContactIdOrderByAtAscIdAsc(contactId).stream()
                .map(t -> new TraceEvent(t.contactId, t.step, t.at, t.tookMs, t.component))
                .toList();
    }
}
