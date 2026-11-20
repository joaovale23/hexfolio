package io.github.joaovale23.hexfolio.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.github.joaovale23.hexfolio.domain.model.ContactStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_message")
class ContactMessageEntity {

    @Id
    UUID id;
    String name;
    String email;
    String message;
    @Enumerated(EnumType.STRING)
    ContactStatus status;
    @Column(name = "received_at")
    Instant receivedAt;
    @Column(name = "updated_at")
    Instant updatedAt;
}
