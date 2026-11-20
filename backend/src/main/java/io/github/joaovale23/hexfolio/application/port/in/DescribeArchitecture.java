package io.github.joaovale23.hexfolio.application.port.in;

import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap;

@InboundPort("Devolve o mapa real de portas e adaptadores, desenhado no modo dev")
public interface DescribeArchitecture {

    ArchitectureMap describe();
}
