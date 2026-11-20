insert into profile (lang, name, role, location, status, bio, tags) values
('en', 'João Vitor Vale', 'Full Stack Developer · Java & Spring Boot', 'Guarujá – SP, Brazil', 'Open to new opportunities',
 'I am the only developer at ZeroUm Mobilidade, where I shipped six systems from scratch in six months: Java 21 + Spring Boot 3 back-ends with hexagonal architecture and over 1,100 automated tests, React front-ends and mobile apps. I use AI in development with a method: spec, plan, tests and review.',
 array['Java back-end', 'hexagonal architecture', 'automated tests', 'full stack', 'AI-assisted development']);

insert into experience (lang, position, company, role, period, location, description, highlights) values
('en', 1, 'ZeroUm Mobilidade (LVL Participações)', 'Junior Systems Analyst, working as Full Stack Developer', 'Jun 2026 – present', 'São Vicente – SP, Brazil',
 'Sole developer at an urban mobility company operating in 9 cities: I gather requirements with the board, design the architecture, implement, test and deploy. I take part in Scrum, with Jira, alongside the outsourced team that builds the main ride-hailing app.',
 array['Two Java 21 + Spring Boot 3 back-ends (ServiJá and conTroll), with Spring Security/JWT, JPA, Flyway, Redis and over 1,100 tests.',
       'Pradis: bulk messaging and support over the official WhatsApp API, with a serverless queue (Cloud Tasks + Cloud Functions), chatbot and role-based access.',
       'Driver screening: background checks across 8 external sources, with a processing queue and spreadsheet batches.',
       'Sintonia: Flutter + Firebase app published on Google Play.',
       'Gas distribution ERP (in production): geofenced time tracking, cylinder control and a delivery app with voice navigation.']),
('en', 2, 'Concept Coworking', 'Administrative Assistant', 'Feb 2026 – Jun 2026', 'Guarujá – SP, Brazil',
 'Office administration while studying Systems Analysis and Development.', '{}');

insert into project (lang, position, slug, name, description, highlights, tags, status, repo_url, demo_url) values
('en', 1, 'hexfolio', 'hexfolio', 'This portfolio: its layout is the architecture of the system that serves it.',
 array['Java 25 + Spring Boot 4.2 with hexagonal architecture; the architecture map is generated from the code.',
       'Asynchronous contact form with AWS SQS, a DLQ and an idempotent consumer.',
       'Tested with JUnit 5, Mockito, Testcontainers, LocalStack and ArchUnit.'],
 array['Java 25', 'Spring Boot 4', 'AWS SQS', 'PostgreSQL', 'React'], 'in progress', 'https://github.com/joaovale23/hexfolio', null),
('en', 2, 'servija', 'ServiJá', 'Services marketplace with back-end, admin panel and mobile app.',
 array['Java 21 + Spring Boot 3 with 15 business modules: auth, bookings, payments, chat and auditing.',
       '939 back-end tests: 604 unit (JUnit 5 + Mockito) and 335 integration (Testcontainers + PostgreSQL).',
       'PostgreSQL + Flyway (34 migrations), Redis, AWS S3 and Prometheus metrics.',
       'GitHub Actions CI, Docker Compose and 21 ADRs.'],
 array['Java 21', 'Spring Boot 3', 'PostgreSQL', 'Redis', 'Testcontainers', 'React'], 'in production', null, null),
('en', 3, 'controll', 'conTroll', 'Ticketing platform (helpdesk) with SLAs, priorities and an audit trail.',
 array['Hexagonal architecture: domain, use cases, ports and adapters.',
       'Security audit of Spring Security + JWT, with five fixes applied.',
       'Structured logs, metrics and health checks; 167 back-end and 57 front-end tests.'],
 array['Java 21', 'Spring Boot', 'Hexagonal', 'Spring Security', 'React'], 'in production', null, null),
('en', 4, 'etl-ans', 'ETL ANS', 'ETL pipeline over public Brazilian health-insurance data, with a FastAPI REST API and a Vue panel.', '{}',
 array['Python', 'PostgreSQL', 'FastAPI', 'Vue.js'], 'done', 'https://github.com/joaovale23/etl-ans', 'https://etl-ans.vercel.app/'),
('en', 5, 'locvac', 'LocVac', 'Mobile app that replaces the paper vaccination card.', '{}',
 array['React Native', 'TypeScript', 'Java'], 'academic', 'https://github.com/claraneves23/LocVac', null);

insert into skill_group (lang, position, title, skills) values
('en', 1, 'Back-end', array['Java', 'Spring Boot', 'Spring Security', 'Hibernate / JPA', 'Flyway', 'Maven', 'PostgreSQL', 'Redis']),
('en', 2, 'Testing', array['JUnit 5', 'Mockito', 'Testcontainers', 'ArchUnit']),
('en', 3, 'Architecture', array['Hexagonal', 'Clean Architecture', 'DDD', 'SOLID', 'ADRs']),
('en', 4, 'Front-end & mobile', array['React', 'TypeScript', 'Next.js', 'Flutter', 'React Native']),
('en', 5, 'DevOps & cloud', array['Docker', 'GitHub Actions', 'AWS (S3, SQS)', 'Google Cloud', 'Firebase', 'Linux']),
('en', 6, 'AI in development', array['Spec-driven development', 'Claude Code', 'Prompt engineering', 'LLM integration']);

insert into education (lang, position, course, institution, period) values
('en', 1, 'Associate degree in Systems Analysis and Development', 'FATEC Rubens Lara', '2023 – 2026'),
('en', 2, 'Apprenticeship Program', 'CAMPS Santos', 'completed Mar 2026'),
('en', 3, 'Languages', 'Portuguese (native) · English and Spanish (intermediate) · French (basic)', '');
