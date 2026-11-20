package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.DescribeArchitecture;
import io.github.joaovale23.hexfolio.application.port.out.ArchitectureSource;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap;

import java.util.Objects;

/** O código não muda com o sistema no ar, então o mapa é lido uma vez e reaproveitado. */
@UseCase("Monta o mapa da arquitetura uma vez e o reaproveita")
public class DescribeArchitectureService implements DescribeArchitecture {

    private final ArchitectureSource source;
    private volatile ArchitectureMap cached;

    public DescribeArchitectureService(ArchitectureSource source) {
        this.source = Objects.requireNonNull(source);
    }

    @Override
    public ArchitectureMap describe() {
        ArchitectureMap map = cached;
        if (map == null) {
            map = source.read();
            cached = map;
        }
        return map;
    }
}
