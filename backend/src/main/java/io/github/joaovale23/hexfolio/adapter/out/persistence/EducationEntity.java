package io.github.joaovale23.hexfolio.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "education")
class EducationEntity {

    @Id
    Long id;
    String lang;
    int position;
    String course;
    String institution;
    String period;
}
