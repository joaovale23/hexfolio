package io.github.joaovale23.hexfolio.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactMessageTest {

    private static final Instant NOW = Instant.parse("2026-11-20T15:00:00Z");

    private static ContactMessage valid() {
        return ContactMessage.receive(UUID.randomUUID(), "Ana", "ana@empresa.com", "Olá, João! Vamos conversar?", NOW);
    }

    @Nested
    class Validacao {

        @Test
        void aceitaMensagemValidaERemoveEspacosDasPontas() {
            ContactMessage message = ContactMessage.receive(UUID.randomUUID(), "  Ana  ", " ana@empresa.com ",
                    "  Olá, João! Vamos conversar?  ", NOW);

            assertThat(message.name()).isEqualTo("Ana");
            assertThat(message.email()).isEqualTo("ana@empresa.com");
            assertThat(message.message()).isEqualTo("Olá, João! Vamos conversar?");
            assertThat(message.status()).isEqualTo(ContactStatus.RECEIVED);
            assertThat(message.receivedAt()).isEqualTo(NOW);
        }

        @Test
        void listaTodosOsCamposInvalidosDeUmaVez() {
            assertThatThrownBy(() -> ContactMessage.receive(UUID.randomUUID(), "A", "nao-e-email", "curta", NOW))
                    .isInstanceOfSatisfying(InvalidContactException.class, e ->
                            assertThat(e.fieldErrors()).containsOnlyKeys("name", "email", "message"));
        }

        @Test
        void trataCamposNulosComoVazios() {
            assertThatThrownBy(() -> ContactMessage.receive(UUID.randomUUID(), null, null, null, NOW))
                    .isInstanceOf(InvalidContactException.class);
        }

        @Test
        void rejeitaMensagemAcimaDoLimite() {
            String longa = "x".repeat(ContactMessage.MESSAGE_MAX + 1);
            assertThatThrownBy(() -> ContactMessage.receive(UUID.randomUUID(), "Ana", "ana@empresa.com", longa, NOW))
                    .isInstanceOfSatisfying(InvalidContactException.class, e ->
                            assertThat(e.fieldErrors()).containsOnlyKeys("message"));
        }
    }

    @Nested
    class CicloDeVida {

        @Test
        void percorreOCaminhoFeliz() {
            ContactMessage message = valid();
            message.markEnqueued();
            message.markProcessing();
            message.markNotified();

            assertThat(message.status()).isEqualTo(ContactStatus.NOTIFIED);
            assertThat(message.status().isFinal()).isTrue();
        }

        @Test
        void permiteNovaTentativaDeProcessamento() {
            ContactMessage message = valid();
            message.markEnqueued();
            message.markProcessing();
            message.markProcessing();

            assertThat(message.status()).isEqualTo(ContactStatus.PROCESSING);
        }

        @Test
        void naoProcessaAntesDeEntrarNaFila() {
            ContactMessage message = valid();

            assertThatThrownBy(message::markProcessing)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("RECEIVED → PROCESSING");
        }

        @Test
        void naoSaiDeUmEstadoFinal() {
            ContactMessage message = valid();
            message.markFailed();

            assertThatThrownBy(message::markEnqueued).isInstanceOf(IllegalStateException.class);
        }
    }
}
