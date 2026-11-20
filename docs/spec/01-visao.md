# 01 · Visão

## Problema

O código dos sistemas que desenvolvo na ZeroUm é privado. Quem avalia meu perfil para uma vaga Java não encontra nenhum código meu, em Java, que prove o que o currículo diz: arquitetura hexagonal, testes automatizados, mensageria e CI.

## Proposta

O **hexfolio** é meu portfólio e, ao mesmo tempo, meu projeto Java público de vitrine.

- **Modo normal:** uma página que se entende em 30 segundos. Um hexágono com meu nome no centro e seis lados: Sobre, Experiência, Projetos, Habilidades, Formação e Contato.
- **Modo dev:** o mesmo hexágono passa a mostrar a arquitetura real do backend que serve a página. No centro ficam o domínio e os casos de uso, nas bordas as portas e por fora os adaptadores. Cada peça leva ao arquivo correspondente no GitHub.
- **Contato ao vivo:** no modo dev, quem envia uma mensagem pelo formulário vê o caminho dela acender no hexágono: HTTP → caso de uso → fila SQS → consumidor → e-mail, com o tempo real de cada etapa.

> O layout do portfólio é a arquitetura do sistema que o serve.

## Públicos

| Público | O que precisa | Como o hexfolio atende |
|---|---|---|
| Recrutador | Entender quem sou em 30 s, inclusive no celular | Modo normal, texto direto, lista simples no celular |
| Avaliador técnico | Ver código Java real, testes e decisões | Modo dev, links para o código, ADRs e CI visível |
| Eu | Receber os contatos | Fila SQS com consumidor e notificação por e-mail |

## Critérios de sucesso

1. O conteúdo aparece em menos de 2 s, mesmo com a API fora do ar, usando a cópia estática.
2. No celular, toda a informação fica legível sem precisar do hexágono.
3. O modo dev mostra só classes que existem. O descritor da arquitetura é gerado a partir do código ([ADR 0004](../adr/0004-arquitetura-gerada-do-codigo.md)).
4. Um contato enviado chega no meu e-mail em menos de 1 minuto, e uma falha vai para a DLQ depois de 3 tentativas.
5. O CI roda os testes unitários, de integração (Testcontainers + LocalStack) e de arquitetura (ArchUnit) a cada push.

## Fora do escopo (v1)

- Painel administrativo: o conteúdo muda por migration ([02-dominio](02-dominio.md)).
- Login de visitantes.
- Idiomas além de português e inglês.
