package io.github.joaovale23.hexfolio.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "experience")
class ExperienceEntity {

    @Id
    Long id;
    String lang;
    int position;
    String company;
    String role;
    String period;
    String location;
    String description;
    @JdbcTypeCode(SqlTypes.ARRAY)
    List<String> highlights;
}
