package io.github.joaovale23.hexfolio.application.port.in;

import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@InboundPort("Devolve as etapas já percorridas por uma mensagem de contato")
public interface TraceContact {

    /** Vazio se a mensagem não existe. */
    Optional<List<TraceEvent>> trace(UUID contactId);
}
