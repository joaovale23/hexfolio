package io.github.joaovale23.hexfolio.adapter.in.messaging;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.github.joaovale23.hexfolio.application.port.in.ProcessContact;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Consome a DLQ: a mensagem esgotou as tentativas, então o contato é marcado como falho. */
@Component
@InboundAdapter("Consome a DLQ e registra a desistência depois das tentativas")
class SqsDeadLetterListener {

    private final ProcessContact processContact;

    SqsDeadLetterListener(ProcessContact processContact) {
        this.processContact = processContact;
    }

    @SqsListener("${hexfolio.queues.contact-dlq}")
    void onMessage(String contactId) {
        processContact.giveUp(UUID.fromString(contactId.strip()));
    }
}
