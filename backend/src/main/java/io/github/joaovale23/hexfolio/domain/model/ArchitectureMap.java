package io.github.joaovale23.hexfolio.domain.model;

import io.github.joaovale23.hexfolio.domain.architecture.DomainModel;

import java.util.List;

/** O desenho do hexágono no modo dev: as peças da arquitetura e as ligações entre elas. */
@DomainModel("Mapa de portas, adaptadores e casos de uso, gerado a partir do código")
public record ArchitectureMap(List<Component> components, List<Link> links) {

    public ArchitectureMap {
        components = List.copyOf(components);
        links = List.copyOf(links);
    }

    public enum Kind { DOMAIN_MODEL, USE_CASE, INBOUND_PORT, OUTBOUND_PORT, INBOUND_ADAPTER, OUTBOUND_ADAPTER }

    public enum Relation { IMPLEMENTS, USES }

    /** Uma peça do hexágono. {@code id} é o nome simples da classe. */
    public record Component(String id, Kind kind, String description, String className, String sourceUrl) {
    }

    /** {@code from} implementa ou usa {@code to}. */
    public record Link(String from, String to, Relation relation) {
    }
}
