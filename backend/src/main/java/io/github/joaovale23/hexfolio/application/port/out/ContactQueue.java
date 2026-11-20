package io.github.joaovale23.hexfolio.application.port.out;

import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;

import java.util.UUID;

@OutboundPort("Publica uma mensagem de contato para processamento assíncrono")
public interface ContactQueue {

    void publish(UUID contactId);
}
