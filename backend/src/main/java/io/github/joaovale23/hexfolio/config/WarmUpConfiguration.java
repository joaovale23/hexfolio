package io.github.joaovale23.hexfolio.config;

import io.github.joaovale23.hexfolio.application.port.in.DescribeArchitecture;
import io.github.joaovale23.hexfolio.application.port.in.GetPortfolio;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Aquece a aplicação logo depois de subir: na máquina gratuita do Render (0,1 CPU), a primeira consulta
 * levaria mais de 10 s inicializando Hibernate, JPA e Jackson. Assim quem paga esse custo é a inicialização,
 * não o primeiro visitante. Roda numa virtual thread para não atrasar o health check.
 */
@Configuration(proxyBeanMethods = false)
class WarmUpConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WarmUpConfiguration.class);

    private final GetPortfolio getPortfolio;
    private final DescribeArchitecture describeArchitecture;

    WarmUpConfiguration(GetPortfolio getPortfolio, DescribeArchitecture describeArchitecture) {
        this.getPortfolio = getPortfolio;
        this.describeArchitecture = describeArchitecture;
    }

    @EventListener(ApplicationReadyEvent.class)
    void warmUp() {
        Thread.ofVirtual().name("warm-up").start(() -> {
            long start = System.nanoTime();
            try {
                for (Lang lang : Lang.values()) {
                    getPortfolio.get(lang);
                }
                describeArchitecture.describe();
                LOG.info("Aquecimento concluído em {} ms", (System.nanoTime() - start) / 1_000_000);
            } catch (RuntimeException e) {
                LOG.warn("Aquecimento falhou; a primeira requisição vai ser mais lenta", e);
            }
        });
    }
}
