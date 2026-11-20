package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.application.port.in.GetPortfolio;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/portfolio")
@InboundAdapter("GET /api/portfolio: entrega o conteúdo dos seis lados do hexágono")
class PortfolioController {

    private final GetPortfolio getPortfolio;

    PortfolioController(GetPortfolio getPortfolio) {
        this.getPortfolio = getPortfolio;
    }

    @GetMapping
    ResponseEntity<PortfolioResponse> get(@RequestParam(defaultValue = "pt") String lang) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(PortfolioResponse.from(getPortfolio.get(Lang.fromCode(lang))));
    }
}
