package io.github.joaovale23.hexfolio.application.port.in;

import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;

import java.util.UUID;

@InboundPort("Processa uma mensagem que saiu da fila: notifica ou desiste dela")
public interface ProcessContact {

    /** Notifica a chegada da mensagem. Idempotente: uma mensagem já notificada não é notificada de novo. */
    void process(UUID contactId);

    /** Chamado quando a mensagem esgotou as tentativas e chegou na DLQ. */
    void giveUp(UUID contactId);
}
