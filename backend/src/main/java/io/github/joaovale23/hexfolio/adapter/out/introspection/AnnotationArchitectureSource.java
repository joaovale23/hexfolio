package io.github.joaovale23.hexfolio.adapter.out.introspection;

import io.github.joaovale23.hexfolio.application.port.out.ArchitectureSource;
import io.github.joaovale23.hexfolio.domain.architecture.DomainModel;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap.Component;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap.Kind;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap.Link;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap.Relation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Descobre as peças da arquitetura lendo as anotações do próprio código (ADR 0004).
 * As ligações vêm do que cada classe implementa e do que recebe no construtor:
 * o desenho do modo dev é, literalmente, o grafo de dependências.
 */
@org.springframework.stereotype.Component
@OutboundAdapter("Lê as anotações do próprio código para desenhar o hexágono")
class AnnotationArchitectureSource implements ArchitectureSource {

    private static final Map<Class<? extends Annotation>, Kind> KINDS = new LinkedHashMap<>();

    static {
        KINDS.put(DomainModel.class, Kind.DOMAIN_MODEL);
        KINDS.put(UseCase.class, Kind.USE_CASE);
        KINDS.put(InboundPort.class, Kind.INBOUND_PORT);
        KINDS.put(OutboundPort.class, Kind.OUTBOUND_PORT);
        KINDS.put(InboundAdapter.class, Kind.INBOUND_ADAPTER);
        KINDS.put(OutboundAdapter.class, Kind.OUTBOUND_ADAPTER);
    }

    private final String basePackage;
    private final String sourceBaseUrl;

    AnnotationArchitectureSource(@Value("${hexfolio.architecture.base-package:io.github.joaovale23.hexfolio}") String basePackage,
                                 @Value("${hexfolio.architecture.source-base-url}") String sourceBaseUrl) {
        this.basePackage = basePackage;
        this.sourceBaseUrl = sourceBaseUrl.endsWith("/") ? sourceBaseUrl : sourceBaseUrl + "/";
    }

    @Override
    public ArchitectureMap read() {
        List<Class<?>> classes = scan();
        Set<Class<?>> known = new LinkedHashSet<>(classes);

        List<Component> components = classes.stream()
                .map(this::toComponent)
                .sorted(Comparator.comparing(Component::kind).thenComparing(Component::id))
                .toList();

        List<Link> links = new ArrayList<>();
        for (Class<?> type : classes) {
            for (Class<?> implemented : type.getInterfaces()) {
                if (known.contains(implemented)) {
                    links.add(new Link(type.getSimpleName(), implemented.getSimpleName(), Relation.IMPLEMENTS));
                }
            }
            for (Class<?> dependency : constructorDependencies(type)) {
                if (known.contains(dependency) && kindOf(dependency) != Kind.DOMAIN_MODEL) {
                    links.add(new Link(type.getSimpleName(), dependency.getSimpleName(), Relation.USES));
                }
            }
        }
        return new ArchitectureMap(components, links.stream().distinct().toList());
    }

    private List<Class<?>> scan() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
                return definition.getMetadata().isIndependent(); // inclui interfaces (portas) e records
            }
        };
        // Só classes anotadas diretamente: quem apenas implementa uma porta anotada (um dublê de teste, por exemplo) fica de fora.
        KINDS.keySet().forEach(annotation -> scanner.addIncludeFilter(new AnnotationTypeFilter(annotation, false, false)));

        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition definition : scanner.findCandidateComponents(basePackage)) {
            classes.add(ClassUtils.resolveClassName(definition.getBeanClassName(), getClass().getClassLoader()));
        }
        return classes;
    }

    private Component toComponent(Class<?> type) {
        Kind kind = kindOf(type);
        String description = KINDS.entrySet().stream()
                .filter(e -> e.getValue() == kind)
                .map(e -> describe(type.getAnnotation(e.getKey())))
                .findFirst().orElse("");
        String path = type.getName().replace('.', '/') + ".java";
        return new Component(type.getSimpleName(), kind, description, type.getName(), sourceBaseUrl + path);
    }

    private static Kind kindOf(Class<?> type) {
        return KINDS.entrySet().stream()
                .filter(e -> type.isAnnotationPresent(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    private static String describe(Annotation annotation) {
        try {
            return (String) annotation.annotationType().getMethod("value").invoke(annotation);
        } catch (ReflectiveOperationException e) {
            return "";
        }
    }

    private static List<Class<?>> constructorDependencies(Class<?> type) {
        return Arrays.stream(type.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .map(c -> List.of(c.getParameterTypes()))
                .orElse(List.of());
    }
}
