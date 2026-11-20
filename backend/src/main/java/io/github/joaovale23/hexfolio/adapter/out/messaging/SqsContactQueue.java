package io.github.joaovale23.hexfolio.adapter.out.messaging;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.github.joaovale23.hexfolio.application.port.out.ContactQueue;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Publica só o id da mensagem: o conteúdo fica no banco, e a fila não carrega dados pessoais. */
@Component
@OutboundAdapter("Publica o id da mensagem na fila AWS SQS")
class SqsContactQueue implements ContactQueue {

    private final SqsTemplate sqs;
    private final String queue;

    SqsContactQueue(SqsTemplate sqs, @Value("${hexfolio.queues.contact}") String queue) {
        this.sqs = sqs;
        this.queue = queue;
    }

    @Override
    public void publish(UUID contactId) {
        sqs.send(to -> to.queue(queue).payload(contactId.toString()));
    }
}
