package io.github.joaovale23.hexfolio.config;

import io.github.joaovale23.hexfolio.application.port.in.DescribeArchitecture;
import io.github.joaovale23.hexfolio.application.port.in.GetPortfolio;
import io.github.joaovale23.hexfolio.application.port.in.ProcessContact;
import io.github.joaovale23.hexfolio.application.port.in.SubmitContact;
import io.github.joaovale23.hexfolio.application.port.in.TraceContact;
import io.github.joaovale23.hexfolio.application.port.out.ArchitectureSource;
import io.github.joaovale23.hexfolio.application.port.out.ContactQueue;
import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.application.port.out.Notifier;
import io.github.joaovale23.hexfolio.application.port.out.PortfolioCatalog;
import io.github.joaovale23.hexfolio.application.service.DescribeArchitectureService;
import io.github.joaovale23.hexfolio.application.service.GetPortfolioService;
import io.github.joaovale23.hexfolio.application.service.ProcessContactService;
import io.github.joaovale23.hexfolio.application.service.SubmitContactService;
import io.github.joaovale23.hexfolio.application.service.TraceContactService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.UUID;

/**
 * Monta os casos de uso. Eles não conhecem o Spring: é aqui, e só aqui, que portas e adaptadores se encontram.
 */
@Configuration(proxyBeanMethods = false)
class UseCaseConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    GetPortfolio getPortfolio(PortfolioCatalog catalog) {
        return new GetPortfolioService(catalog);
    }

    @Bean
    SubmitContact submitContact(ContactRepository repository, ContactQueue queue, Clock clock) {
        return new SubmitContactService(repository, queue, clock, UUID::randomUUID);
    }

    @Bean
    ProcessContact processContact(ContactRepository repository, Notifier notifier, Clock clock) {
        return new ProcessContactService(repository, notifier, clock);
    }

    @Bean
    TraceContact traceContact(ContactRepository repository) {
        return new TraceContactService(repository);
    }

    @Bean
    DescribeArchitecture describeArchitecture(ArchitectureSource source) {
        return new DescribeArchitectureService(source);
    }
}
