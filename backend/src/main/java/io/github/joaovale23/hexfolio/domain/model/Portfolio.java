package io.github.joaovale23.hexfolio.domain.model;

import io.github.joaovale23.hexfolio.domain.architecture.DomainModel;

import java.util.List;
import java.util.Objects;

/** O portfólio inteiro em um idioma: o que o modo normal mostra nos seis lados do hexágono. */
@DomainModel("Portfólio em um idioma: perfil, experiências, projetos, habilidades e formação")
public record Portfolio(
        Lang lang,
        Profile profile,
        List<Experience> experiences,
        List<Project> projects,
        List<SkillGroup> skillGroups,
        List<Education> education) {

    public Portfolio {
        Objects.requireNonNull(lang, "lang");
        Objects.requireNonNull(profile, "profile");
        experiences = List.copyOf(experiences);
        projects = List.copyOf(projects);
        skillGroups = List.copyOf(skillGroups);
        education = List.copyOf(education);
    }

    public record Profile(String name, String role, String location, String status, String bio, List<String> tags) {
        public Profile {
            tags = List.copyOf(tags);
        }
    }

    public record Experience(String company, String role, String period, String location,
                             String description, List<String> highlights) {
        public Experience {
            highlights = List.copyOf(highlights);
        }
    }

    /**
     * Projeto ou estudo de caso. Projetos com código da empresa não têm {@code repoUrl}:
     * o front mostra "código privado" no lugar do link.
     */
    public record Project(String slug, String name, String description, List<String> highlights,
                          List<String> tags, String status, String repoUrl, String demoUrl) {
        public Project {
            highlights = List.copyOf(highlights);
            tags = List.copyOf(tags);
        }

        public boolean privateCode() {
            return repoUrl == null || repoUrl.isBlank();
        }
    }

    public record SkillGroup(String title, List<String> skills) {
        public SkillGroup {
            skills = List.copyOf(skills);
        }
    }

    public record Education(String course, String institution, String period) {
    }
}
