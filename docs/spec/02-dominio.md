# 02 · Domínio

## Modelo

| Agregado | O que representa | Campos principais |
|---|---|---|
| `Profile` | Quem sou | nome, cargo, local, status, bio, tags (por idioma) |
| `Experience` | Empregos | empresa, cargo, período, local, descrição, destaques |
| `Project` | Projetos e estudos de caso | slug, nome, descrição, destaques, tags, status, `repoUrl` opcional, `isPrivate` |
| `SkillGroup` | Habilidades agrupadas | título, lista de habilidades |
| `Education` | Formação e idiomas | curso, instituição, período |
| `ContactMessage` | Mensagem enviada pelo formulário | id, nome, e-mail, texto, status, `receivedAt` |

Os textos têm versão em `pt` e em `en`. O idioma vem do parâmetro `?lang=`, e o padrão é `pt`.

O conteúdo (Profile, Experience, Project, SkillGroup e Education) fica no PostgreSQL e muda por **migration Flyway**. Cada alteração do portfólio vira um commit revisável.

## Ciclo de vida do contato

```
RECEIVED → ENQUEUED → PROCESSING → NOTIFIED
                          ↘ FAILED (depois de 3 tentativas, vai para a DLQ)
```

Cada transição grava um `TraceEvent` (etapa, instante, duração). É isso que o front anima no modo dev.

## Casos de uso (portas de entrada)

| Porta | Caso de uso | Quem chama |
|---|---|---|
| `GetPortfolio` | Devolve o portfólio inteiro em um idioma | Adaptador REST |
| `SubmitContact` | Valida, grava e enfileira uma mensagem | Adaptador REST |
| `ProcessContact` | Consome da fila e notifica | Adaptador SQS (consumidor) |
| `TraceContact` | Devolve os eventos de uma mensagem | Adaptador REST (SSE) |
| `DescribeArchitecture` | Devolve o mapa de portas e adaptadores | Adaptador REST |

> O consumidor SQS é um **adaptador de entrada**: é ele que aciona `ProcessContact`. A fila aparece duas vezes no hexágono, como saída (publicar) e como entrada (consumir).

## Portas de saída

| Porta | Responsabilidade | Adaptador |
|---|---|---|
| `PortfolioCatalog` | Ler o conteúdo | PostgreSQL (Spring Data JPA) |
| `ContactRepository` | Gravar mensagens e eventos | PostgreSQL |
| `ContactQueue` | Publicar uma mensagem para processamento | AWS SQS |
| `Notifier` | Avisar que chegou um contato | E-mail (SMTP) |

## Regras

- O domínio não importa Spring, JPA, AWS nem Jackson. Um teste ArchUnit garante isso.
- A mensagem de contato precisa de nome (2–80 caracteres), e-mail válido e texto (10–2.000 caracteres).
- O processamento é **idempotente**: se a mesma mensagem chegar duas vezes da fila, ela notifica uma vez só.
