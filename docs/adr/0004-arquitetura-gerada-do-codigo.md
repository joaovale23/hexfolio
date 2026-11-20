# ADR 0004 · Mapa da arquitetura gerado a partir do código

**Status:** aceita · 2026-11-20

## Contexto

O modo dev desenha a arquitetura do backend. Se esse desenho for mantido à mão, ele vai ficar desatualizado, e um portfólio que mostra uma arquitetura falsa é pior do que não mostrar nenhuma.

## Decisão

Cada classe relevante recebe uma anotação própria: `@UseCase`, `@InboundPort`, `@OutboundPort`, `@InboundAdapter` ou `@OutboundAdapter`, com uma descrição curta. Na inicialização, `DescribeArchitecture` lê essas anotações e monta o mapa que o front desenha. A URL de cada classe no GitHub é gerada a partir do nome do pacote.

Um teste ArchUnit garante que:

- todo adaptador implementa uma porta ou depende de uma porta;
- toda classe em `adapter.*` tem uma anotação de adaptador.

## Consequências

- O desenho do modo dev não tem como mentir: se a classe não existir, ela não aparece.
- As anotações ficam no domínio como metadados puros, sem dependência de framework.
