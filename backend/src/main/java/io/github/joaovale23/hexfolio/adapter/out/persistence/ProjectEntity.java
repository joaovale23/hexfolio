package io.github.joaovale23.hexfolio.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "project")
class ProjectEntity {

    @Id
    Long id;
    String lang;
    int position;
    String slug;
    String name;
    String description;
    @JdbcTypeCode(SqlTypes.ARRAY)
    List<String> highlights;
    @JdbcTypeCode(SqlTypes.ARRAY)
    List<String> tags;
    String status;
    @Column(name = "repo_url")
    String repoUrl;
    @Column(name = "demo_url")
    String demoUrl;
}
