package io.github.joaovale23.hexfolio.domain.model;

import java.util.Map;

/** Mensagem de contato que não respeita as regras do domínio. Carrega o erro de cada campo. */
public class InvalidContactException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public InvalidContactException(Map<String, String> fieldErrors) {
        super("Mensagem de contato inválida: " + fieldErrors.keySet());
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    public Map<String, String> fieldErrors() {
        return fieldErrors;
    }
}
