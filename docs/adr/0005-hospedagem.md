# ADR 0005 · Hospedagem

**Status:** proposta · 2026-11-20

## Decisão proposta

| Peça | Escolha | Por quê |
|---|---|---|
| API | Fly.io, região `gru` | Já uso no trabalho, imagem Docker, a máquina dorme quando ninguém acessa |
| Banco | Neon (PostgreSQL serverless, plano gratuito) | Gratuito, PostgreSQL de verdade, compatível com Flyway |
| Fila | AWS SQS `sa-east-1` | [ADR 0002](0002-sqs-para-contato.md) |
| E-mail | SMTP (definir o provedor) | Isolado atrás da porta `Notifier` |
| Front | GitHub Pages | Gratuito, deploy pelo GitHub Actions |

## Em aberto

- **Provedor de e-mail:** Gmail com senha de app ou um serviço transacional (Resend, Brevo). Fica para quando chegarmos ao deploy.
- **Domínio próprio** (`joaovale.dev`, por exemplo): opcional, para depois da v1.

## Consequências

- Custo esperado: R$ 0 por mês dentro dos níveis gratuitos.
- A primeira requisição depois de um tempo parado pode levar alguns segundos. A cópia estática do front ([05-frontend](../spec/05-frontend.md)) cobre isso.
