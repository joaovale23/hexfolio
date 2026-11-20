# ADR 0003 · Java 25 e Spring Boot 4.2

**Status:** aceita · 2026-11-20

## Contexto

No trabalho uso Java 21 e Spring Boot 3. O hexfolio é um projeto novo, sem legado.

## Decisão

Usar **Java 25** (LTS) e **Spring Boot 4.2**, as versões atuais.

## Consequências

- O projeto mostra que acompanho a evolução da plataforma, e a migração do Boot 3 para o 4 vira assunto de entrevista.
- Algumas bibliotecas podem demorar a suportar o Boot 4. Se alguma essencial não tiver suporte, o fallback é o Boot 3.5 com Java 21, registrado num novo ADR.
- Virtual threads ficam ligadas (`spring.threads.virtual.enabled=true`), o que combina bem com o SSE do trace.
