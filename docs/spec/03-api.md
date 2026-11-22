# 03 · API

Base: `/api`. As respostas são JSON, e os erros seguem o **RFC 9457** (Problem Details). A documentação OpenAPI fica em `/api/docs`.

## Endpoints

| Método e rota | Caso de uso | Resposta |
|---|---|---|
| `GET /api/portfolio?lang=pt` | `GetPortfolio` | `200` com profile, experiences, projects, skills e education |
| `POST /api/contact` | `SubmitContact` | `202 Accepted` com `{ "id": "…", "traceUrl": "/api/contact/{id}/trace" }` |
| `GET /api/contact/{id}/trace` | `TraceContact` | `text/event-stream`: um evento por etapa, fecha ao chegar em `NOTIFIED` ou `FAILED`, ou depois de 30 s |
| `GET /api/architecture` | `DescribeArchitecture` | `200` com o domínio, as portas e os adaptadores, e para cada um a classe e a URL no GitHub |
| `GET /actuator/health` | — | Health check para o Render |

## Exemplos

`POST /api/contact`

```json
{ "name": "Ana Recrutadora", "email": "ana@empresa.com", "message": "Olá, João! Gostaria de conversar sobre uma vaga.", "website": "" }
```

`website` é um campo isca (honeypot): fica invisível para pessoas, e se vier preenchido a requisição é descartada em silêncio com `202`.

Evento SSE de `/trace`:

```
event: step
data: {"step":"ENQUEUED","at":"2026-11-20T15:02:11.204Z","tookMs":38,"component":"ContactQueue"}
```

`component` é a porta ou o caso de uso envolvido na etapa (`SubmitContact`, `ContactQueue`, `ProcessContact`, `Notifier`). O front cruza esse nome com `GET /api/architecture` para acender a porta e o adaptador que a implementa. Assim o domínio não precisa conhecer os nomes dos adaptadores.

Etapas possíveis: `RECEIVED`, `ENQUEUED`, `PROCESSING`, `ATTEMPT_FAILED`, `NOTIFIED`, `FAILED`.

## Proteções

- **Limite de envio:** 5 contatos por IP a cada hora (`429` com `Retry-After`).
- **CORS:** só a origem do front publicado e `localhost` em desenvolvimento.
- **Validação:** feita no domínio (`ContactMessage.receive`), com erro `400` que lista todos os campos inválidos de uma vez.
