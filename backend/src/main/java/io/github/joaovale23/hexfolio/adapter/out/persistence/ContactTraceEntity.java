package io.github.joaovale23.hexfolio.adapter.out.persistence;

import io.github.joaovale23.hexfolio.domain.model.TraceEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_trace")
class ContactTraceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "contact_id")
    UUID contactId;
    @Enumerated(EnumType.STRING)
    TraceEvent.Step step;
    Instant at;
    @Column(name = "took_ms")
    long tookMs;
    String component;
}
