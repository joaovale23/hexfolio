# ADR 0001 · Arquitetura hexagonal

**Status:** aceita · 2026-11-20

## Contexto

O hexfolio existe para mostrar como eu projeto backends. A arquitetura também é o elemento visual do portfólio: o hexágono do front é o diagrama do backend.

## Decisão

Usar portas e adaptadores (arquitetura hexagonal), em um único módulo Maven com estes pacotes:

```
io.github.joaovale23.hexfolio
├── domain                 modelo e regras, sem nenhum framework
├── application
│   ├── port.in            casos de uso (interfaces)
│   ├── port.out           o que o domínio precisa do mundo externo
│   └── service            implementação dos casos de uso
├── adapter
│   ├── in.web             REST, SSE
│   ├── in.messaging       consumidor SQS
│   ├── out.persistence    JPA + PostgreSQL
│   ├── out.messaging      publicador SQS
│   └── out.notification   SMTP
└── config                 montagem dos beans
```

As regras de dependência são verificadas por testes ArchUnit no CI.

## Consequências

- O domínio é testado sem Spring, e esses testes rodam em milissegundos.
- Trocar o SQS ou o SMTP não toca no domínio.
- Há mais classes do que num CRUD em camadas. É um custo aceito, porque mostrar essa separação é o objetivo do projeto.
- Um único módulo Maven, e não um módulo por camada, porque o ArchUnit já garante as fronteiras e o projeto fica mais simples de ler.
