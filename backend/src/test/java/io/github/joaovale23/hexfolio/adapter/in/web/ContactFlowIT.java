package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.IntegrationTest;
import io.github.joaovale23.hexfolio.TestcontainersConfiguration.RecordingNotifier;
import io.github.joaovale23.hexfolio.application.port.out.ContactRepository;
import io.github.joaovale23.hexfolio.domain.model.ContactStatus;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import io.github.joaovale23.hexfolio.domain.model.TraceEvent.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** O caminho completo: HTTP → caso de uso → SQS (LocalStack) → consumidor → notificação. */
@IntegrationTest
class ContactFlowIT {

    private static final String VALID = """
            {"name": "Ana Recrutadora", "email": "ana@empresa.com", "message": "Olá, João! Gostaria de conversar sobre uma vaga."}
            """;

    @Autowired
    MockMvc mvc;
    @Autowired
    RecordingNotifier notifier;
    @Autowired
    ContactRepository repository;

    @BeforeEach
    void reset() {
        notifier.failing.set(false);
    }

    @Test
    void mensagemPercorreAFilaEChegaNoEmail() throws Exception {
        UUID id = submit(VALID);

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(repository.findById(id)).hasValueSatisfying(m ->
                        assertThat(m.status()).isEqualTo(ContactStatus.NOTIFIED)));

        assertThat(notifier.sent).anySatisfy(m -> assertThat(m.id()).isEqualTo(id));
        assertThat(repository.traceOf(id)).extracting(TraceEvent::step)
                .containsExactly(Step.RECEIVED, Step.ENQUEUED, Step.PROCESSING, Step.NOTIFIED);
    }

    @Test
    void traceTransmiteAsEtapasPorSse() throws Exception {
        UUID id = submit(VALID);

        MvcResult result = mvc.perform(get("/api/contact/{id}/trace", id))
                .andExpect(request().asyncStarted())
                .andReturn();
        result.getAsyncResult(15_000);

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("event:step", "\"step\":\"RECEIVED\"", "\"step\":\"NOTIFIED\"", "\"component\":\"Notifier\"");
    }

    @Test
    void depoisDasTentativasAMensagemVaiParaADlq() throws Exception {
        notifier.failing.set(true);
        UUID id = submit(VALID);

        await().atMost(Duration.ofSeconds(40)).untilAsserted(() ->
                assertThat(repository.findById(id)).hasValueSatisfying(m ->
                        assertThat(m.status()).isEqualTo(ContactStatus.FAILED)));

        assertThat(repository.traceOf(id)).extracting(TraceEvent::step)
                .contains(Step.ATTEMPT_FAILED)
                .endsWith(Step.FAILED);
        assertThat(repository.traceOf(id)).filteredOn(e -> e.step() == Step.ATTEMPT_FAILED).hasSize(3);
    }

    @Test
    void recusaMensagemInvalidaComProblemDetails() throws Exception {
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "A", "email": "nao-e-email", "message": "curta"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Mensagem inválida"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    void campoIscaPreenchidoEDescartadoEmSilencio() throws Exception {
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Robô", "email": "bot@spam.com", "message": "Compre agora, oferta imperdível!", "website": "spam.com"}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.traceUrl").doesNotExist());

        assertThat(notifier.sent).noneSatisfy(m -> assertThat(m.email()).isEqualTo("bot@spam.com"));
    }

    @Test
    void traceDeMensagemInexistenteDa404() throws Exception {
        mvc.perform(get("/api/contact/{id}/trace", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void idInvalidoNoTraceDa400() throws Exception {
        mvc.perform(get("/api/contact/{id}/trace", "nao-e-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content -> assertThat(content.getResponse().getContentAsString()).contains("id"));
    }

    private UUID submit(String json) throws Exception {
        String body = mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.traceUrl", containsString("/trace")))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(body.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1").strip());
    }
}
