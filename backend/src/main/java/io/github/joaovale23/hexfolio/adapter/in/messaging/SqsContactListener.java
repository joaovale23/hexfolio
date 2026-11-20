package io.github.joaovale23.hexfolio.adapter.in.messaging;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.github.joaovale23.hexfolio.application.port.in.ProcessContact;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consome a fila de contato. Se o caso de uso lançar uma exceção, a mensagem não é confirmada:
 * o SQS entrega de novo depois do visibility timeout e, na terceira falha, manda para a DLQ.
 */
@Component
@InboundAdapter("Consome a fila AWS SQS e aciona o processamento do contato")
class SqsContactListener {

    private final ProcessContact processContact;

    SqsContactListener(ProcessContact processContact) {
        this.processContact = processContact;
    }

    @SqsListener("${hexfolio.queues.contact}")
    void onMessage(String contactId) {
        processContact.process(UUID.fromString(contactId.strip()));
    }
}
