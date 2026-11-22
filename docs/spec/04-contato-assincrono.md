# 04 · Contato assíncrono

## Fluxo

```
Visitante ──POST /api/contact──▶ ContactController            (adaptador de entrada · HTTP)
                                   │
                                   ▼
                              SubmitContactService          (caso de uso)
                                   │ grava RECEIVED
                                   ▼
                              SqsContactQueue ──▶ fila hexfolio-contact      (adaptador de saída)
                                                       │
                                                       ▼
                              SqsContactListener         (adaptador de entrada · fila)
                                   │
                                   ▼
                              ProcessContactService       (caso de uso)
                                   │
                                   ▼
                              SmtpNotifier ──▶ e-mail para mim             (adaptador de saída)
```

## Decisões

- **Por que fila, se o envio é baixo?** Para o visitante não esperar o SMTP e para uma falha de e-mail não perder a mensagem. Esse é exatamente o padrão que a vaga pede (SQS). Mais detalhes no [ADR 0002](../adr/0002-sqs-para-contato.md).
- **Tentativas:** a fila tem `maxReceiveCount = 3`. Depois da terceira falha, a mensagem vai para a `hexfolio-contact-dlq`, o status vira `FAILED` e o evento aparece no trace.
- **Idempotência:** antes de notificar, o consumidor confere o status no banco. Se já estiver `NOTIFIED`, só confirma a mensagem na fila.
- **Visibilidade:** cada etapa grava um `TraceEvent` com a duração, que o SSE devolve ao front.

## Ambientes

| Ambiente | Fila | E-mail |
|---|---|---|
| Testes (CI) | LocalStack via Testcontainers | Adaptador falso que guarda os e-mails em memória |
| Local | LocalStack via Docker Compose | Mailpit (interface web em `localhost:8025`) |
| Produção | AWS SQS (`us-east-1`) | Brevo (SMTP), credenciais nas variáveis do Render |
