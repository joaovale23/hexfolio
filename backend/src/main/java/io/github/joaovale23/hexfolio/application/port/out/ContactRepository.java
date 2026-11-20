package io.github.joaovale23.hexfolio.application.port.out;

import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@OutboundPort("Grava mensagens de contato e as etapas do caminho de cada uma")
public interface ContactRepository {

    void save(ContactMessage message);

    Optional<ContactMessage> findById(UUID id);

    void append(TraceEvent event);

    List<TraceEvent> traceOf(UUID contactId);
}
