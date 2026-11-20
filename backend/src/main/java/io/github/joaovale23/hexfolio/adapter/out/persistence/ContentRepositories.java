package io.github.joaovale23.hexfolio.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ProfileJpaRepository extends JpaRepository<ProfileEntity, String> {
}

interface ExperienceJpaRepository extends JpaRepository<ExperienceEntity, Long> {
    List<ExperienceEntity> findByLangOrderByPosition(String lang);
}

interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findByLangOrderByPosition(String lang);
}

interface SkillGroupJpaRepository extends JpaRepository<SkillGroupEntity, Long> {
    List<SkillGroupEntity> findByLangOrderByPosition(String lang);
}

interface EducationJpaRepository extends JpaRepository<EducationEntity, Long> {
    List<EducationEntity> findByLangOrderByPosition(String lang);
}
