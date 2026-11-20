package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** O mapa que o modo dev desenha sai do código: estas ligações existem porque as classes existem. */
@IntegrationTest
class ArchitectureApiIT {

    @Autowired
    MockMvc mvc;

    @Test
    void descreveAsPecasEAsLigacoesReais() throws Exception {
        mvc.perform(get("/api/architecture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components[?(@.id == 'SqsContactListener')].kind").value("INBOUND_ADAPTER"))
                .andExpect(jsonPath("$.components[?(@.id == 'ContactQueue')].kind").value("OUTBOUND_PORT"))
                .andExpect(jsonPath("$.components[?(@.id == 'ContactMessage')].kind").value("DOMAIN_MODEL"))
                .andExpect(jsonPath("$.components[?(@.id == 'SqsContactQueue')].sourceUrl",
                        hasItem(startsWith("https://github.com/joaovale23/hexfolio/blob/main/backend/src/main/java/io/github/"))))
                .andExpect(jsonPath("$.links[?(@.from == 'SqsContactListener' && @.to == 'ProcessContact')].relation").value("USES"))
                .andExpect(jsonPath("$.links[?(@.from == 'SubmitContactService' && @.to == 'SubmitContact')].relation").value("IMPLEMENTS"))
                .andExpect(jsonPath("$.links[?(@.from == 'SubmitContactService' && @.to == 'ContactQueue')].relation").value("USES"))
                .andExpect(jsonPath("$.links[?(@.from == 'SqsContactQueue' && @.to == 'ContactQueue')].relation").value("IMPLEMENTS"));
    }
}
