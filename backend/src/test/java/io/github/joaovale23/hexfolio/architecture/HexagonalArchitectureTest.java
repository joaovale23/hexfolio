package io.github.joaovale23.hexfolio.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/** As regras da ADR 0001 e da ADR 0004, verificadas a cada build. */
@AnalyzeClasses(packages = "io.github.joaovale23.hexfolio", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    private static final String[] FRAMEWORKS = {
            "org.springframework..", "jakarta..", "org.hibernate..", "tools.jackson..", "com.fasterxml..",
            "software.amazon..", "io.awspring.."};

    @ArchTest
    static final ArchRule camadas = onionArchitecture()
            .domainModels("..domain..")
            .domainServices("..application.port..")
            .applicationServices("..application.service..")
            .adapter("web", "..adapter.in.web..")
            .adapter("messaging-in", "..adapter.in.messaging..")
            .adapter("persistence", "..adapter.out.persistence..")
            .adapter("messaging-out", "..adapter.out.messaging..")
            .adapter("notification", "..adapter.out.notification..")
            .adapter("introspection", "..adapter.out.introspection..")
            .ignoreDependency(resideInAPackage("..config.."), alwaysTrue())
            .because("o domínio fica no centro e os adaptadores só se conhecem pelas portas");

    @ArchTest
    static final ArchRule dominioSemFramework = noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
            .because("o domínio é Java puro (ADR 0001)");

    @ArchTest
    static final ArchRule casosDeUsoSemFramework = noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
            .because("os casos de uso são montados em config e não conhecem o Spring");

    @ArchTest
    static final ArchRule servicosSaoCasosDeUso = classes().that().resideInAPackage("..application.service..")
            .and().arePublic().and().areNotNestedClasses()
            .should().beAnnotatedWith(UseCase.class);

    @ArchTest
    static final ArchRule portasDeEntradaAnotadas = classes().that().resideInAPackage("..application.port.in..")
            .and().areInterfaces().and().areNotNestedClasses()
            .should().beAnnotatedWith(InboundPort.class);

    @ArchTest
    static final ArchRule portasDeSaidaAnotadas = classes().that().resideInAPackage("..application.port.out..")
            .and().areInterfaces()
            .should().beAnnotatedWith(OutboundPort.class);

    @ArchTest
    static final ArchRule adaptadoresDeEntradaAnotados = classes().that().resideInAPackage("..adapter.in..")
            .and(annotatedWith(RestController.class).or(annotatedWith(Component.class)))
            .should().beAnnotatedWith(InboundAdapter.class)
            .because("o modo dev só desenha o que está anotado (ADR 0004)");

    @ArchTest
    static final ArchRule adaptadoresDeSaidaAnotados = classes().that().resideInAPackage("..adapter.out..")
            .and().implement(describe("uma porta de saída", (JavaClass c) -> c.isAnnotatedWith(OutboundPort.class)))
            .should().beAnnotatedWith(OutboundAdapter.class)
            .because("o modo dev só desenha o que está anotado (ADR 0004)");

    @ArchTest
    static void nenhumCicloEntrePacotes(JavaClasses classes) {
        slices()
                .matching("io.github.joaovale23.hexfolio.(*)..")
                .should().beFreeOfCycles()
                .check(classes);
    }
}
