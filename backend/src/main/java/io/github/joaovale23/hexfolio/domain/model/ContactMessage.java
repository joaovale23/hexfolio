package io.github.joaovale23.hexfolio.domain.model;

import io.github.joaovale23.hexfolio.domain.architecture.DomainModel;

import java.time.Instant;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Mensagem enviada pelo formulário de contato. As regras de validação e as transições
 * de status vivem aqui, sem depender de framework.
 */
@DomainModel("Mensagem de contato, com validação e ciclo de vida")
public final class ContactMessage {

    static final int NAME_MIN = 2;
    static final int NAME_MAX = 80;
    static final int MESSAGE_MIN = 10;
    static final int MESSAGE_MAX = 2000;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");

    private static final Map<ContactStatus, Set<ContactStatus>> ALLOWED = Map.of(
            ContactStatus.RECEIVED, EnumSet.of(ContactStatus.ENQUEUED, ContactStatus.FAILED),
            ContactStatus.ENQUEUED, EnumSet.of(ContactStatus.PROCESSING, ContactStatus.FAILED),
            ContactStatus.PROCESSING, EnumSet.of(ContactStatus.PROCESSING, ContactStatus.NOTIFIED, ContactStatus.FAILED),
            ContactStatus.NOTIFIED, EnumSet.noneOf(ContactStatus.class),
            ContactStatus.FAILED, EnumSet.noneOf(ContactStatus.class));

    private final UUID id;
    private final String name;
    private final String email;
    private final String message;
    private final Instant receivedAt;
    private ContactStatus status;

    private ContactMessage(UUID id, String name, String email, String message, Instant receivedAt, ContactStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = name;
        this.email = email;
        this.message = message;
        this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt");
        this.status = Objects.requireNonNull(status, "status");
    }

    /** Cria uma mensagem nova, validando os campos. Lança {@link InvalidContactException} com todos os erros. */
    public static ContactMessage receive(UUID id, String name, String email, String message, Instant now) {
        String cleanName = trim(name);
        String cleanEmail = trim(email);
        String cleanMessage = trim(message);

        Map<String, String> errors = new LinkedHashMap<>();
        if (cleanName.length() < NAME_MIN || cleanName.length() > NAME_MAX) {
            errors.put("name", "deve ter entre %d e %d caracteres".formatted(NAME_MIN, NAME_MAX));
        }
        if (!EMAIL.matcher(cleanEmail).matches()) {
            errors.put("email", "deve ser um e-mail válido");
        }
        if (cleanMessage.length() < MESSAGE_MIN || cleanMessage.length() > MESSAGE_MAX) {
            errors.put("message", "deve ter entre %d e %d caracteres".formatted(MESSAGE_MIN, MESSAGE_MAX));
        }
        if (!errors.isEmpty()) {
            throw new InvalidContactException(errors);
        }
        return new ContactMessage(id, cleanName, cleanEmail, cleanMessage, now, ContactStatus.RECEIVED);
    }

    /** Reconstrói uma mensagem já gravada (usado pelo adaptador de persistência). */
    public static ContactMessage restore(UUID id, String name, String email, String message,
                                         Instant receivedAt, ContactStatus status) {
        return new ContactMessage(id, name, email, message, receivedAt, status);
    }

    public void markEnqueued() {
        moveTo(ContactStatus.ENQUEUED);
    }

    public void markProcessing() {
        moveTo(ContactStatus.PROCESSING);
    }

    public void markNotified() {
        moveTo(ContactStatus.NOTIFIED);
    }

    public void markFailed() {
        moveTo(ContactStatus.FAILED);
    }

    private void moveTo(ContactStatus next) {
        if (!ALLOWED.get(status).contains(next)) {
            throw new IllegalStateException("Transição inválida: %s → %s".formatted(status, next));
        }
        status = next;
    }

    private static String trim(String value) {
        return value == null ? "" : value.strip();
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String message() {
        return message;
    }

    public Instant receivedAt() {
        return receivedAt;
    }

    public ContactStatus status() {
        return status;
    }
}
