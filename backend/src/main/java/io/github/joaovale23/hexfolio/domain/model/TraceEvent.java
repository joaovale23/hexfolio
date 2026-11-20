package io.github.joaovale23.hexfolio.domain.model;

import io.github.joaovale23.hexfolio.domain.architecture.DomainModel;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Uma etapa do caminho de uma mensagem de contato. O modo dev usa {@code component}
 * para acender a peça certa do hexágono: é o nome da porta ou do caso de uso envolvido.
 */
@DomainModel("Etapa registrada no caminho de uma mensagem, usada na animação ao vivo")
public record TraceEvent(UUID contactId, Step step, Instant at, long tookMs, String component) {

    public enum Step {
        RECEIVED, ENQUEUED, PROCESSING, ATTEMPT_FAILED, NOTIFIED, FAILED;

        public boolean isFinal() {
            return this == NOTIFIED || this == FAILED;
        }
    }

    public TraceEvent {
        Objects.requireNonNull(contactId, "contactId");
        Objects.requireNonNull(step, "step");
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(component, "component");
    }
}
