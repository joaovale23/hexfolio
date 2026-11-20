package io.github.joaovale23.hexfolio.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "profile")
class ProfileEntity {

    @Id
    String lang;
    String name;
    String role;
    String location;
    String status;
    String bio;
    @JdbcTypeCode(SqlTypes.ARRAY)
    List<String> tags;
}
