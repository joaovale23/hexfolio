package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class PortfolioApiIT {

    @Autowired
    MockMvc mvc;

    @Test
    void entregaOPortfolioEmPortuguesPorPadrao() throws Exception {
        mvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", startsWith("max-age=")))
                .andExpect(jsonPath("$.lang").value("pt"))
                .andExpect(jsonPath("$.profile.name").value("João Vitor Vale"))
                .andExpect(jsonPath("$.experiences", hasSize(2)))
                .andExpect(jsonPath("$.projects[0].slug").value("hexfolio"))
                .andExpect(jsonPath("$.skillGroups", hasSize(6)))
                .andExpect(jsonPath("$.education", hasSize(3)));
    }

    @Test
    void marcaComoPrivadoOProjetoSemRepositorio() throws Exception {
        mvc.perform(get("/api/portfolio"))
                .andExpect(jsonPath("$.projects[?(@.slug == 'servija')].privateCode").value(true))
                .andExpect(jsonPath("$.projects[?(@.slug == 'etl-ans')].privateCode").value(false));
    }

    @Test
    void entregaEmInglesQuandoPedido() throws Exception {
        mvc.perform(get("/api/portfolio").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lang").value("en"))
                .andExpect(jsonPath("$.profile.role").value("Full Stack Developer · Java & Spring Boot"));
    }
}
