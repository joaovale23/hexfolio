package io.github.joaovale23.hexfolio.domain.architecture;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Conceito central do domínio.
 * Lida em tempo de execução para montar o mapa de arquitetura do modo dev (ADR 0004).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DomainModel {

    /** Descrição curta, exibida no hexágono. */
    String value();
}
