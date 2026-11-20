package io.github.joaovale23.hexfolio.application.port.out;

import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap;

@OutboundPort("Descobre as peças da arquitetura a partir do próprio código")
public interface ArchitectureSource {

    ArchitectureMap read();
}
