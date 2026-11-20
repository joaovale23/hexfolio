# 05 · Front-end

React + TypeScript + Vite, publicado no GitHub Pages. O hexágono é desenhado em SVG.

## Modo normal

- O hexágono fica no centro, com nome e cargo no meio e as seis seções nos lados.
- Ao clicar num lado, um painel abre ao lado do hexágono (no desktop) ou abaixo dele (no tablet).
- Sobre já vem aberto, para que a primeira tela já diga quem sou.

## Modo dev

- Um interruptor fixo no topo: `modo dev`.
- O hexágono se transforma, com animação: o centro vira **Domínio**, as bordas mostram as **portas** e os **adaptadores** aparecem por fora. Os de entrada ficam à esquerda (HTTP, consumidor SQS) e os de saída à direita (PostgreSQL, publicador SQS, SMTP).
- Os dados vêm de `GET /api/architecture`, então o desenho nunca mostra uma classe que não existe.
- Clicar numa peça mostra o nome da classe, a responsabilidade e o link para o código.
- Cada seção do modo normal ganha um rodapé com a requisição que a carregou, o JSON da resposta e o tempo.
- **Contato ao vivo:** depois do envio, o front abre o SSE de trace e acende cada peça na ordem em que os eventos chegam, com o tempo de cada etapa.

## Celular (largura < 768 px)

- Sem hexágono interativo. O topo mostra um hexágono pequeno e estático como marca, e as seções aparecem em lista vertical.
- O modo dev mostra o trace como uma linha do tempo vertical.

## Conteúdo sem API

- No build, um script grava `portfolio.pt.json` e `portfolio.en.json` com o conteúdo do repositório.
- A página mostra essa cópia na hora e troca pelos dados da API quando eles chegam.
- Se a API estiver dormindo (o Fly.io desliga a máquina quando não há acesso), o modo dev avisa: "acordando o servidor…".

## Acessibilidade

- O hexágono é navegável por teclado, e cada lado é um `button` com rótulo.
- As animações respeitam `prefers-reduced-motion`.
- Há tema claro e escuro.
