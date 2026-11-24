-- fintrack: controle financeiro com Angular + Spring Boot, publicado em 2026-11-23. Entra logo depois do hexfolio.
-- As posições sobem em dois passos para não esbarrar na restrição unique (lang, position) no meio do update.
update project set position = position + 100 where position >= 2;
update project set position = position - 99 where position >= 100;

insert into project (lang, position, slug, name, description, highlights, tags, status, repo_url, demo_url) values
('pt', 2, 'fintrack', 'fintrack',
 'Controle financeiro pessoal: lançamento em linguagem natural, metas por categoria e o caminho do dinheiro num diagrama de fluxo.',
 array['Front em Angular 22: signals, httpResource, deferrable views, animações nativas e gráficos em SVG feitos à mão.',
       'API em Java 21 + Spring Boot 4 com JWT (resource server), Flyway, Problem Details (RFC 9457) e importação de extrato CSV/OFX.',
       'Contas recorrentes lançadas por um job idempotente, cofrinhos com metas e entrada como visitante, sem cadastro.',
       '34 testes no backend (JUnit 5, Mockito, Testcontainers) e 49 no front (Vitest).'],
 array['Angular', 'TypeScript', 'Java 21', 'Spring Boot 4', 'PostgreSQL'], 'no ar',
 'https://github.com/joaovale23/fintrack', 'https://joaovale23.github.io/fintrack/'),
('en', 2, 'fintrack', 'fintrack',
 'Personal finance tracker: natural-language entries, per-category budgets and a flow diagram showing where the money went.',
 array['Angular 22 front-end: signals, httpResource, deferrable views, native animations and hand-made SVG charts.',
       'Java 21 + Spring Boot 4 API with JWT (resource server), Flyway, Problem Details (RFC 9457) and CSV/OFX statement import.',
       'Recurring bills posted by an idempotent job, savings goals and guest access with no sign-up.',
       '34 back-end tests (JUnit 5, Mockito, Testcontainers) and 49 front-end tests (Vitest).'],
 array['Angular', 'TypeScript', 'Java 21', 'Spring Boot 4', 'PostgreSQL'], 'live',
 'https://github.com/joaovale23/fintrack', 'https://joaovale23.github.io/fintrack/');

-- Angular passa a abrir o grupo de front-end.
update skill_group set skills = array['Angular', 'React', 'TypeScript', 'Next.js', 'Flutter', 'React Native'] where position = 4;
