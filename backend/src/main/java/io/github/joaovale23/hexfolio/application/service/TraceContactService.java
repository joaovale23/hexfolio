package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.TraceContact;
import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@UseCase("Lê as etapas já percorridas por uma mensagem")
public class TraceContactService implements TraceContact {

    private final ContactRepository repository;

    public TraceContactService(ContactRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Optional<List<TraceEvent>> trace(UUID contactId) {
        return repository.findById(contactId).map(message -> repository.traceOf(contactId));
    }
}
