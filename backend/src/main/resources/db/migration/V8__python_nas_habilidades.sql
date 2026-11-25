-- Python entra no fim do grupo de back-end (já aparece no projeto etl-ans com FastAPI).
update skill_group
   set skills = array['Java', 'Spring Boot', 'Spring Security', 'Hibernate / JPA', 'Flyway', 'Maven', 'PostgreSQL', 'Redis', 'Python']
 where position = 1;
