package io.github.joaovale23.hexfolio.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface ContactMessageJpaRepository extends JpaRepository<ContactMessageEntity, UUID> {
}

interface ContactTraceJpaRepository extends JpaRepository<ContactTraceEntity, Long> {
    List<ContactTraceEntity> findByContactIdOrderByAtAscIdAsc(UUID contactId);
}
