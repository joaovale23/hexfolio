package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Repositório em memória para testar os casos de uso sem banco: é a vantagem de depender de uma porta. */
class InMemoryContactRepository implements ContactRepository {

    final Map<UUID, ContactMessage> messages = new LinkedHashMap<>();
    final List<TraceEvent> events = new ArrayList<>();

    @Override
    public void save(ContactMessage message) {
        messages.put(message.id(), message);
    }

    @Override
    public Optional<ContactMessage> findById(UUID id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public void append(TraceEvent event) {
        events.add(event);
    }

    @Override
    public List<TraceEvent> traceOf(UUID contactId) {
        return events.stream().filter(e -> e.contactId().equals(contactId)).toList();
    }

    List<TraceEvent.Step> stepsOf(UUID contactId) {
        return traceOf(contactId).stream().map(TraceEvent::step).toList();
    }
}
