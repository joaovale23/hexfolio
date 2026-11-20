package io.github.joaovale23.hexfolio.application.port.out;

import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import io.github.joaovale23.hexfolio.domain.model.Portfolio;

@OutboundPort("Lê o conteúdo do portfólio")
public interface PortfolioCatalog {

    Portfolio load(Lang lang);
}
