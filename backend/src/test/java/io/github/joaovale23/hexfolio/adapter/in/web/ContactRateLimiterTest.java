package io.github.joaovale23.hexfolio.adapter.in.web;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ContactRateLimiterTest {

    /** Relógio que o teste avança na mão. */
    static class MutableClock extends Clock {
        Instant now = Instant.parse("2026-11-20T15:00:00Z");

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    private final MutableClock clock = new MutableClock();
    private final ContactRateLimiter limiter = new ContactRateLimiter(2, Duration.ofHours(1), clock);

    @Test
    void bloqueiaDepoisDoLimiteEInformaQuantoFalta() {
        assertThat(limiter.tryAcquire("1.1.1.1")).isEmpty();
        clock.now = clock.now.plus(Duration.ofMinutes(10));
        assertThat(limiter.tryAcquire("1.1.1.1")).isEmpty();

        assertThat(limiter.tryAcquire("1.1.1.1")).contains(Duration.ofMinutes(50));
    }

    @Test
    void liberaQuandoAJanelaPassa() {
        limiter.tryAcquire("1.1.1.1");
        limiter.tryAcquire("1.1.1.1");
        clock.now = clock.now.plus(Duration.ofHours(1));

        assertThat(limiter.tryAcquire("1.1.1.1")).isEmpty();
    }

    @Test
    void contaCadaIpSeparadamente() {
        limiter.tryAcquire("1.1.1.1");
        limiter.tryAcquire("1.1.1.1");

        assertThat(limiter.tryAcquire("2.2.2.2")).isEmpty();
    }
}
