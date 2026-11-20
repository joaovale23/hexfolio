package io.github.joaovale23.hexfolio.domain.architecture;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adaptador de entrada: traduz um estímulo externo (HTTP, fila) em uma chamada de porta de entrada.
 * Lida em tempo de execução para montar o mapa de arquitetura do modo dev (ADR 0004).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InboundAdapter {

    /** Descrição curta, exibida no hexágono. */
    String value();
}
