package io.github.joaovale23.hexfolio.adapter.out.messaging;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.Map;

/**
 * Cria a fila e a DLQ com a política de reenvio, em desenvolvimento e nos testes (LocalStack).
 * Roda na inicialização dos beans, antes de os consumidores começarem a escutar.
 * Em produção as filas são criadas uma vez na AWS (ver docs/infra/aws.md).
 */
@Component
@ConditionalOnProperty(name = "hexfolio.queues.provision", havingValue = "true")
class SqsQueueProvisioner implements InitializingBean {

    private final SqsAsyncClient sqs;
    private final String queue;
    private final String deadLetterQueue;
    private final int maxReceiveCount;
    private final int visibilityTimeoutSeconds;

    SqsQueueProvisioner(SqsAsyncClient sqs,
                        @Value("${hexfolio.queues.contact}") String queue,
                        @Value("${hexfolio.queues.contact-dlq}") String deadLetterQueue,
                        @Value("${hexfolio.queues.max-receive-count:3}") int maxReceiveCount,
                        @Value("${hexfolio.queues.visibility-timeout-seconds:10}") int visibilityTimeoutSeconds) {
        this.sqs = sqs;
        this.queue = queue;
        this.deadLetterQueue = deadLetterQueue;
        this.maxReceiveCount = maxReceiveCount;
        this.visibilityTimeoutSeconds = visibilityTimeoutSeconds;
    }

    @Override
    public void afterPropertiesSet() {
        String dlqUrl = sqs.createQueue(r -> r.queueName(deadLetterQueue)).join().queueUrl();
        String dlqArn = sqs.getQueueAttributes(r -> r.queueUrl(dlqUrl).attributeNames(QueueAttributeName.QUEUE_ARN))
                .join().attributes().get(QueueAttributeName.QUEUE_ARN);

        String redrivePolicy = "{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":\"%d\"}".formatted(dlqArn, maxReceiveCount);
        sqs.createQueue(r -> r.queueName(queue).attributes(Map.of(
                QueueAttributeName.REDRIVE_POLICY, redrivePolicy,
                QueueAttributeName.VISIBILITY_TIMEOUT, String.valueOf(visibilityTimeoutSeconds)))).join();
    }
}
