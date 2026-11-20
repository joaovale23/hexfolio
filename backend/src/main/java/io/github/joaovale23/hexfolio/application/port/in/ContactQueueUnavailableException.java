package io.github.joaovale23.hexfolio.application.port.in;

/** Faz parte do contrato de {@link SubmitContact}: a fila não aceitou a mensagem. O adaptador web responde 503. */
public class ContactQueueUnavailableException extends RuntimeException {

    public ContactQueueUnavailableException(Throwable cause) {
        super("Fila de contato indisponível", cause);
    }
}
