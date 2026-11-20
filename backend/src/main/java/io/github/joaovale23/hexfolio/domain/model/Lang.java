package io.github.joaovale23.hexfolio.domain.model;

import java.util.Locale;

/** Idiomas em que o portfólio é publicado. */
public enum Lang {
    PT, EN;

    /** Converte o parâmetro {@code ?lang=}; valores desconhecidos ou vazios caem em português. */
    public static Lang fromCode(String code) {
        if (code == null || code.isBlank()) {
            return PT;
        }
        return switch (code.trim().toLowerCase(Locale.ROOT)) {
            case "en" -> EN;
            default -> PT;
        };
    }

    public String code() {
        return name().toLowerCase(Locale.ROOT);
    }
}
