package io.github.joaovale23.hexfolio.adapter.out.persistence;

import io.github.joaovale23.hexfolio.application.port.out.PortfolioCatalog;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.Lang;
import io.github.joaovale23.hexfolio.domain.model.Portfolio;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lê o conteúdo das tabelas versionadas pelo Flyway e converte no modelo de domínio. */
@Component
@OutboundAdapter("Lê o conteúdo do portfólio no PostgreSQL (Spring Data JPA)")
class JpaPortfolioCatalog implements PortfolioCatalog {

    private final ProfileJpaRepository profiles;
    private final ExperienceJpaRepository experiences;
    private final ProjectJpaRepository projects;
    private final SkillGroupJpaRepository skillGroups;
    private final EducationJpaRepository education;

    JpaPortfolioCatalog(ProfileJpaRepository profiles, ExperienceJpaRepository experiences, ProjectJpaRepository projects,
                        SkillGroupJpaRepository skillGroups, EducationJpaRepository education) {
        this.profiles = profiles;
        this.experiences = experiences;
        this.projects = projects;
        this.skillGroups = skillGroups;
        this.education = education;
    }

    @Override
    @Transactional(readOnly = true)
    public Portfolio load(Lang lang) {
        String code = lang.code();
        ProfileEntity p = profiles.findById(code)
                .orElseThrow(() -> new IllegalStateException("Sem perfil cadastrado para o idioma " + code));

        return new Portfolio(
                lang,
                new Portfolio.Profile(p.name, p.role, p.location, p.status, p.bio, nonNull(p.tags)),
                experiences.findByLangOrderByPosition(code).stream()
                        .map(e -> new Portfolio.Experience(e.company, e.role, e.period, e.location, e.description, nonNull(e.highlights)))
                        .toList(),
                projects.findByLangOrderByPosition(code).stream()
                        .map(pr -> new Portfolio.Project(pr.slug, pr.name, pr.description, nonNull(pr.highlights),
                                nonNull(pr.tags), pr.status, pr.repoUrl, pr.demoUrl))
                        .toList(),
                skillGroups.findByLangOrderByPosition(code).stream()
                        .map(s -> new Portfolio.SkillGroup(s.title, nonNull(s.skills)))
                        .toList(),
                education.findByLangOrderByPosition(code).stream()
                        .map(ed -> new Portfolio.Education(ed.course, ed.institution, ed.period))
                        .toList());
    }

    private static List<String> nonNull(List<String> values) {
        return values == null ? List.of() : values;
    }
}
