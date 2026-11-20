package io.github.joaovale23.hexfolio.application.port.in;

import io.github.joaovale23.hexfolio.domain.architecture.InboundPort;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import io.github.joaovale23.hexfolio.domain.model.Portfolio;

@InboundPort("Devolve o portfólio inteiro em um idioma")
public interface GetPortfolio {

    Portfolio get(Lang lang);
}
