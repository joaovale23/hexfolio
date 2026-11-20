package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.application.port.in.DescribeArchitecture;
import io.github.joaovale23.hexfolio.domain.architecture.InboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.ArchitectureMap;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/architecture")
@InboundAdapter("GET /api/architecture: entrega o mapa real de portas e adaptadores")
class ArchitectureController {

    private final DescribeArchitecture describeArchitecture;

    ArchitectureController(DescribeArchitecture describeArchitecture) {
        this.describeArchitecture = describeArchitecture;
    }

    @GetMapping
    ResponseEntity<ArchitectureMap> get() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .body(describeArchitecture.describe());
    }
}
