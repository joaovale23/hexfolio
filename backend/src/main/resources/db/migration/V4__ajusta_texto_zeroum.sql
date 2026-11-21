-- Troca "único desenvolvedor" por "responsável de ponta a ponta": mesma informação, sem passar a ideia de alguém que nunca trabalhou em time.
update profile set bio = 'Na ZeroUm Mobilidade, entreguei em seis meses seis sistemas do zero, da especificação ao deploy: backends em Java 21 + Spring Boot 3 com arquitetura hexagonal e mais de 1.100 testes automatizados, front-end em React e apps mobile. Uso IA no desenvolvimento com método: especificação, plano, testes e revisão.'
where lang = 'pt';

update profile set bio = 'At ZeroUm Mobilidade I shipped six systems from scratch in six months, from spec to deploy: Java 21 + Spring Boot 3 back-ends with hexagonal architecture and over 1,100 automated tests, React front-ends and mobile apps. I use AI in development with a method: spec, plan, tests and review.'
where lang = 'en';

update experience set description = 'Responsável pelos sistemas internos de uma plataforma de mobilidade urbana presente em 9 cidades da Baixada Santista: levanto requisitos com a diretoria, desenho a arquitetura, implemento, testo e faço o deploy. Participo do Scrum, com Jira, junto à equipe que desenvolve o app principal.'
where lang = 'pt' and position = 1;

update experience set description = 'I own the internal systems of an urban mobility company operating in 9 cities: I gather requirements with the board, design the architecture, implement, test and deploy. I take part in Scrum, with Jira, alongside the team that builds the main ride-hailing app.'
where lang = 'en' and position = 1;
