# ADR 0002 · Fila SQS para o formulário de contato

**Status:** aceita · 2026-11-20

## Contexto

Enviar o e-mail dentro da requisição faria o visitante esperar o SMTP, e uma falha do servidor de e-mail perderia a mensagem. O volume é baixo, mas cada contato pode ser uma oportunidade de trabalho.

## Decisão

`POST /api/contact` grava a mensagem e publica na fila **AWS SQS** `hexfolio-contact`. Um consumidor no mesmo serviço processa a fila e envia o e-mail. Depois de 3 falhas, a mensagem vai para a `hexfolio-contact-dlq`.

Biblioteca: **Spring Cloud AWS** (`SqsTemplate` e `@SqsListener`). Testes com **LocalStack** via Testcontainers.

## Alternativas consideradas

- **`@Async` com tentativas em memória:** mais simples, mas perde a mensagem se a máquina reiniciar.
- **Tabela de outbox com job agendado:** confiável, mas não mostra mensageria, que é uma das competências pedidas no mercado.
- **RabbitMQ:** exigiria hospedar um broker. O SQS é gerenciado e cabe no nível gratuito da AWS (1 milhão de requisições por mês).

## Consequências

- É preciso ter uma conta AWS e credenciais de um usuário IAM com acesso só às duas filas.
- O consumidor precisa ser idempotente, porque o SQS entrega cada mensagem *pelo menos uma vez*.
