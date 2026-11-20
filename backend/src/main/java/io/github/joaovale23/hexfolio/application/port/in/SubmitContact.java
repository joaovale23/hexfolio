package io.github.joaovale23.hexfolio.application.port.in;

import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;

import java.util.UUID;

@InboundPort("Valida, grava e enfileira uma mensagem de contato")
public interface SubmitContact {

    UUID submit(Command command);

    record Command(String name, String email, String message) {
    }
}
