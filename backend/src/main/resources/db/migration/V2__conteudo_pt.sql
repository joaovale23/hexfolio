insert into profile (lang, name, role, location, status, bio, tags) values
('pt', 'João Vitor Vale', 'Desenvolvedor Full Stack · Java e Spring Boot', 'Guarujá – SP, Brasil', 'Aberto a novas oportunidades',
 'Sou o único desenvolvedor da ZeroUm Mobilidade, onde em seis meses entreguei seis sistemas do zero: backends em Java 21 + Spring Boot 3 com arquitetura hexagonal e mais de 1.100 testes automatizados, front-end em React e apps mobile. Uso IA no desenvolvimento com método: especificação, plano, testes e revisão.',
 array['back-end Java', 'arquitetura hexagonal', 'testes automatizados', 'full stack', 'IA no desenvolvimento']);

insert into experience (lang, position, company, role, period, location, description, highlights) values
('pt', 1, 'ZeroUm Mobilidade (LVL Participações)', 'Analista de Sistemas Júnior, atuando como Desenvolvedor Full Stack', 'jun/2026 – atual', 'São Vicente – SP',
 'Único desenvolvedor da empresa (mobilidade urbana em 9 cidades da Baixada Santista): levanto requisitos com a diretoria, desenho a arquitetura, implemento, testo e faço o deploy. Participo do Scrum, com Jira, junto à equipe terceirizada do app principal.',
 array['Dois backends em Java 21 + Spring Boot 3 (ServiJá e conTroll), com Spring Security/JWT, JPA, Flyway, Redis e mais de 1.100 testes.',
       'Pradis: disparo e atendimento pela API oficial do WhatsApp, com fila serverless (Cloud Tasks + Cloud Functions), chatbot e permissões por papel.',
       'Checagem de motoristas: antecedentes por CPF em 8 fontes externas, com fila de processamento e lote por planilha.',
       'Sintonia: app Flutter + Firebase publicado na Google Play.',
       'ERP de revenda de gás (em produção): ponto por geolocalização, controle de vasilhames e app do entregador com navegação por voz.']),
('pt', 2, 'Concept Coworking', 'Assistente Administrativo', 'fev/2026 – jun/2026', 'Guarujá – SP',
 'Rotinas administrativas do escritório, em paralelo à graduação em Análise e Desenvolvimento de Sistemas.', '{}');

insert into project (lang, position, slug, name, description, highlights, tags, status, repo_url, demo_url) values
('pt', 1, 'hexfolio', 'hexfolio', 'Este portfólio: o layout é a arquitetura do sistema que o serve.',
 array['Java 25 + Spring Boot 4.2 em arquitetura hexagonal, com o mapa da arquitetura gerado a partir do código.',
       'Formulário de contato assíncrono com AWS SQS, DLQ e consumidor idempotente.',
       'Testes com JUnit 5, Mockito, Testcontainers, LocalStack e ArchUnit.'],
 array['Java 25', 'Spring Boot 4', 'AWS SQS', 'PostgreSQL', 'React'], 'em construção', 'https://github.com/joaovale23/hexfolio', null),
('pt', 2, 'servija', 'ServiJá', 'Marketplace de serviços com backend, painel administrativo e app.',
 array['Java 21 + Spring Boot 3 com 15 módulos de negócio: autenticação, agendamentos, pagamentos, chat e auditoria.',
       '939 testes no backend: 604 unitários (JUnit 5 + Mockito) e 335 de integração (Testcontainers + PostgreSQL).',
       'PostgreSQL + Flyway (34 migrations), Redis, AWS S3 e métricas no Prometheus.',
       'CI no GitHub Actions, Docker Compose e 21 ADRs.'],
 array['Java 21', 'Spring Boot 3', 'PostgreSQL', 'Redis', 'Testcontainers', 'React'], 'em produção', null, null),
('pt', 3, 'controll', 'conTroll', 'Plataforma de chamados (helpdesk) com SLA, prioridades e trilha de auditoria.',
 array['Arquitetura hexagonal: domínio, casos de uso, portas e adaptadores.',
       'Auditoria de segurança em Spring Security + JWT, com cinco correções aplicadas.',
       'Logs estruturados, métricas e health checks; 167 testes no backend e 57 no front.'],
 array['Java 21', 'Spring Boot', 'Hexagonal', 'Spring Security', 'React'], 'em produção', null, null),
('pt', 4, 'etl-ans', 'ETL ANS', 'Pipeline ETL com dados públicos da ANS, API REST em FastAPI e painel web em Vue.', '{}',
 array['Python', 'PostgreSQL', 'FastAPI', 'Vue.js'], 'concluído', 'https://github.com/joaovale23/etl-ans', 'https://etl-ans.vercel.app/'),
('pt', 5, 'locvac', 'LocVac', 'Aplicativo móvel que substitui a carteira de vacinação em papel.', '{}',
 array['React Native', 'TypeScript', 'Java'], 'acadêmico', 'https://github.com/claraneves23/LocVac', null);

insert into skill_group (lang, position, title, skills) values
('pt', 1, 'Back-end', array['Java', 'Spring Boot', 'Spring Security', 'Hibernate / JPA', 'Flyway', 'Maven', 'PostgreSQL', 'Redis']),
('pt', 2, 'Testes', array['JUnit 5', 'Mockito', 'Testcontainers', 'ArchUnit']),
('pt', 3, 'Arquitetura', array['Hexagonal', 'Clean Architecture', 'DDD', 'SOLID', 'ADRs']),
('pt', 4, 'Front-end e mobile', array['React', 'TypeScript', 'Next.js', 'Flutter', 'React Native']),
('pt', 5, 'DevOps e nuvem', array['Docker', 'GitHub Actions', 'AWS (S3, SQS)', 'Google Cloud', 'Firebase', 'Linux']),
('pt', 6, 'IA no desenvolvimento', array['Spec-driven development', 'Claude Code', 'Engenharia de prompt', 'Integração com LLMs']);

insert into education (lang, position, course, institution, period) values
('pt', 1, 'Tecnólogo em Análise e Desenvolvimento de Sistemas', 'FATEC Rubens Lara', '2023 – 2026'),
('pt', 2, 'Programa de Aprendizagem', 'CAMPS Santos', 'concluído em mar/2026'),
('pt', 3, 'Idiomas', 'Português nativo · inglês e espanhol intermediários · francês básico', '');
