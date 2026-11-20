package io.github.joaovale23.hexfolio.adapter.in.web;

import io.github.joaovale23.hexfolio.domain.model.Portfolio;

import java.util.List;

/** Contrato público de GET /api/portfolio. Separado do domínio para a API poder evoluir sem mexer nele. */
record PortfolioResponse(String lang, Profile profile, List<Experience> experiences, List<Project> projects,
                         List<SkillGroup> skillGroups, List<Education> education) {

    record Profile(String name, String role, String location, String status, String bio, List<String> tags) {
    }

    record Experience(String company, String role, String period, String location, String description,
                      List<String> highlights) {
    }

    record Project(String slug, String name, String description, List<String> highlights, List<String> tags,
                   String status, String repoUrl, String demoUrl, boolean privateCode) {
    }

    record SkillGroup(String title, List<String> skills) {
    }

    record Education(String course, String institution, String period) {
    }

    static PortfolioResponse from(Portfolio p) {
        var profile = p.profile();
        return new PortfolioResponse(
                p.lang().code(),
                new Profile(profile.name(), profile.role(), profile.location(), profile.status(), profile.bio(), profile.tags()),
                p.experiences().stream()
                        .map(e -> new Experience(e.company(), e.role(), e.period(), e.location(), e.description(), e.highlights()))
                        .toList(),
                p.projects().stream()
                        .map(pr -> new Project(pr.slug(), pr.name(), pr.description(), pr.highlights(), pr.tags(),
                                pr.status(), pr.repoUrl(), pr.demoUrl(), pr.privateCode()))
                        .toList(),
                p.skillGroups().stream().map(s -> new SkillGroup(s.title(), s.skills())).toList(),
                p.education().stream().map(e -> new Education(e.course(), e.institution(), e.period())).toList());
    }
}
