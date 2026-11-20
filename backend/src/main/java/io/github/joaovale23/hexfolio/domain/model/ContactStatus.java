package io.github.joaovale23.hexfolio.domain.model;

/**
 * Ciclo de vida de uma mensagem de contato.
 * <pre>
 * RECEIVED → ENQUEUED → PROCESSING → NOTIFIED
 *                           ↘ FAILED (depois das tentativas, vai para a DLQ)
 * </pre>
 */
public enum ContactStatus {
    RECEIVED, ENQUEUED, PROCESSING, NOTIFIED, FAILED;

    public boolean isFinal() {
        return this == NOTIFIED || this == FAILED;
    }
}
