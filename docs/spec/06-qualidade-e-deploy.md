# 06 · Qualidade e deploy

## Testes

| Camada | Ferramentas | O que cobre |
|---|---|---|
| Domínio e casos de uso | JUnit 5, Mockito, AssertJ | Regras, validação, transições de status, idempotência |
| Adaptadores | Testcontainers (PostgreSQL, LocalStack) | Repositórios JPA, migrations Flyway, publicação e consumo no SQS, DLQ |
| API | `@SpringBootTest` + `MockMvc` | Contratos, Problem Details, limite de envio, SSE |
| Arquitetura | ArchUnit | O domínio não depende de frameworks, os adaptadores só falam com as portas, e todas as classes de `/api/architecture` existem |
| Front | Vitest + Testing Library | Hexágono, alternância de modo, linha do tempo do trace |

## CI (GitHub Actions)

`build → testes unitários → testes de integração → ArchUnit → relatório de cobertura (JaCoCo) → imagem Docker`

O build do front e o deploy no GitHub Pages rodam num workflow separado.

## Deploy

| Peça | Onde |
|---|---|
| API | Fly.io, região `gru` (São Paulo), imagem Docker |
| Banco | PostgreSQL gerenciado (ver [ADR 0005](../adr/0005-hospedagem.md)) |
| Fila | AWS SQS `sa-east-1`, com DLQ |
| Front | GitHub Pages |

## Observabilidade

- Logs estruturados em JSON, com o `contactId` em todas as linhas do fluxo.
- Actuator: `health` e `metrics`, incluindo a contagem de contatos por status.
