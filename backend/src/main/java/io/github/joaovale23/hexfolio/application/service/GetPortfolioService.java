package io.github.joaovale23.hexfolio.application.service;

import io.github.joaovale23.hexfolio.application.port.in.GetPortfolio;
import io.github.joaovale23.hexfolio.application.port.out.PortfolioCatalog;
import io.github.joaovale23.hexfolio.domain.architecture.UseCase;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import io.github.joaovale23.hexfolio.domain.model.Portfolio;

import java.util.Objects;

@UseCase("Monta o portfólio no idioma pedido")
public class GetPortfolioService implements GetPortfolio {

    private final PortfolioCatalog catalog;

    public GetPortfolioService(PortfolioCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog);
    }

    @Override
    public Portfolio get(Lang lang) {
        return catalog.load(lang == null ? Lang.PT : lang);
    }
}
