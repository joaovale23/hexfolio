package io.github.joaovale23.hexfolio.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class LangTest {

    @ParameterizedTest
    @CsvSource({"pt, PT", "en, EN", "EN, EN", " en , EN", "es, PT"})
    void converteOCodigoDoIdioma(String code, Lang expected) {
        assertThat(Lang.fromCode(code)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void usaPortuguesQuandoNaoHaIdioma(String code) {
        assertThat(Lang.fromCode(code)).isEqualTo(Lang.PT);
    }
}
