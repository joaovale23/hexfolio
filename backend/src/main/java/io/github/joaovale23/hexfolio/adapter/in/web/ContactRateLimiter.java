package io.github.joaovale23.hexfolio.adapter.in.web;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Janela deslizante em memória: no máximo {@code limit} envios por IP a cada {@code window}.
 * Basta para uma única máquina; com várias instâncias, o estado iria para o Redis.
 */
class ContactRateLimiter {

    private final int limit;
    private final Duration window;
    private final Clock clock;
    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();

    ContactRateLimiter(int limit, Duration window, Clock clock) {
        this.limit = limit;
        this.window = window;
        this.clock = clock;
    }

    /** Vazio se o envio é permitido; senão, quanto tempo falta para liberar. */
    Optional<Duration> tryAcquire(String key) {
        Instant now = clock.instant();
        Deque<Instant> times = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (times) {
            while (!times.isEmpty() && !times.peekFirst().isAfter(now.minus(window))) {
                times.pollFirst();
            }
            if (times.size() >= limit) {
                return Optional.of(Duration.between(now, times.peekFirst().plus(window)));
            }
            times.addLast(now);
            return Optional.empty();
        }
    }
}
