package io.github.joaovale23.hexfolio;

import io.github.joaovale23.hexfolio.application.port.out.Notifier;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Infra real dos testes de integração: PostgreSQL e SQS (LocalStack) em containers.
 * O e-mail é o único substituto: um {@link RecordingNotifier} que guarda as mensagens e pode simular falhas.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"));
    }

    @Bean
    @ServiceConnection
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4")).withServices("sqs");
    }

    @Bean
    @Primary
    RecordingNotifier recordingNotifier() {
        return new RecordingNotifier();
    }

    public static class RecordingNotifier implements Notifier {

        public final List<ContactMessage> sent = new CopyOnWriteArrayList<>();
        public final AtomicBoolean failing = new AtomicBoolean(false);

        @Override
        public void notify(ContactMessage message) {
            if (failing.get()) {
                throw new IllegalStateException("SMTP simulado fora do ar");
            }
            sent.add(message);
        }
    }
}
