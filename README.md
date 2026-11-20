# hexfolio

> O layout do meu portfólio é a arquitetura do sistema que o serve.

**hexfolio** é o portfólio de João Vitor Vale e também um backend Java público: Java 25, Spring Boot 4.2 e arquitetura hexagonal.

- **Modo normal:** um hexágono com seis seções (Sobre, Experiência, Projetos, Habilidades, Formação e Contato).
- **Modo dev:** o mesmo hexágono mostra as portas e os adaptadores reais do backend. Ao enviar o formulário de contato, você vê a mensagem percorrer HTTP → caso de uso → AWS SQS → consumidor → e-mail, ao vivo.

## Status

🚧 Em desenvolvimento. O projeto começa pela especificação:

| Documento | Conteúdo |
|---|---|
| [01 · Visão](docs/spec/01-visao.md) | Problema, públicos, critérios de sucesso |
| [02 · Domínio](docs/spec/02-dominio.md) | Modelo, casos de uso, portas |
| [03 · API](docs/spec/03-api.md) | Endpoints e contratos |
| [04 · Contato assíncrono](docs/spec/04-contato-assincrono.md) | Fluxo com SQS, DLQ e idempotência |
| [05 · Front-end](docs/spec/05-frontend.md) | Hexágono, modo dev, celular |
| [06 · Qualidade e deploy](docs/spec/06-qualidade-e-deploy.md) | Testes, CI, hospedagem |

Decisões de arquitetura: [`docs/adr`](docs/adr).

## Rodando o backend

Requer Java 25 e Docker.

```bash
cd backend
./mvnw verify                                          # testes unitários, ArchUnit e integração (Testcontainers)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local  # sobe PostgreSQL, LocalStack e Mailpit via Docker Compose
```

Com o perfil `local`:

| Endereço | O que é |
|---|---|
| `http://localhost:8080/api/portfolio` | Conteúdo do portfólio (`?lang=en` para inglês) |
| `http://localhost:8080/api/architecture` | Mapa de portas e adaptadores, gerado a partir do código |
| `http://localhost:8080/api/docs` | Documentação OpenAPI (Swagger UI) |
| `http://localhost:8025` | Mailpit: os e-mails de contato chegam aqui |

## Estrutura

```
backend/src/main/java/io/github/joaovale23/hexfolio
├── domain                 modelo e regras, Java puro
├── application
│   ├── port.in            casos de uso (interfaces)
│   ├── port.out           o que o domínio precisa do mundo externo
│   └── service            implementação dos casos de uso, sem Spring
├── adapter
│   ├── in.web             REST e SSE
│   ├── in.messaging       consumidores SQS (fila e DLQ)
│   ├── out.persistence    JPA + PostgreSQL
│   ├── out.messaging      publicador SQS
│   ├── out.notification   SMTP
│   └── out.introspection  lê as anotações do código para desenhar o hexágono
└── config                 o único lugar onde portas e adaptadores se encontram
```

## Stack

Java 25 · Spring Boot 4.2 · Spring Data JPA · PostgreSQL · Flyway · Spring Cloud AWS (SQS) · JUnit 5 · Mockito · Testcontainers · LocalStack · ArchUnit · React · TypeScript · Vite · GitHub Actions · Docker · Fly.io
