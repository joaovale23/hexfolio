# 05 · Front-end

React + TypeScript + Vite, publicado no GitHub Pages. O hexágono é desenhado em SVG.

## Modo normal: editorial claro

- Fundo creme, títulos em serifa (Fraunces), texto em Inter.
- À esquerda: cargo, nome grande, uma frase de apresentação e, logo abaixo, o conteúdo da seção escolhida.
- À direita: o hexágono dividido em **seis fatias de cores sólidas**, uma por seção. A fatia ativa salta para fora. O centro tem as iniciais.
- Sobre já vem aberto, para que a primeira tela já diga quem sou.

## Modo dev: planta técnica (blueprint)

- Um interruptor fixo no topo: `modo dev`. A página escurece em azul de planta, com grade.
- O diagrama vem de `GET /api/architecture` e é organizado em colunas, na ordem em que uma requisição atravessa o sistema: adaptador de entrada → porta de entrada → caso de uso → domínio → porta de saída → adaptador de saída. Cada caso de uso fica na linha da porta que implementa. As larguras saem dos nomes reais, e nomes longos quebram em duas linhas.
- **Painel lateral sempre visível**, sem rolagem da página:
  - **Peça selecionada:** tipo, descrição, ligações e link para o código. Quando a peça é um controller que já respondeu nesta página (`PortfolioController`, `ArchitectureController`), o painel mostra a requisição real: rota, status, tempo, se veio da API ou da cópia estática, e o JSON.
  - **Teste ao vivo:** o formulário de contato. Depois do envio, o formulário dá lugar à linha do tempo das etapas, com o tempo de cada uma.
- **Contato ao vivo:** o front abre o SSE de trace e acende cada peça na ordem dos eventos, segurando cada etapa por 750 ms para dar para acompanhar. Uma falha acende em vermelho.

## Celular (largura < 768 px)

- Um hexágono pequeno e estático de seis cores como marca, e as seções em lista vertical.
- O formulário de contato mostra a linha do tempo das etapas.

## Conteúdo sem API

- `npm run snapshot` grava `public/snapshot/portfolio.pt.json`, `portfolio.en.json` e `architecture.json` a partir da API.
- A página mostra essa cópia na hora e troca pelos dados da API quando eles chegam.
- No modo dev, a requisição mostra "da cópia estática · acordando o servidor…" enquanto a API não responde.

## Acessibilidade

- As fatias e as peças são `role="button"`, navegáveis por teclado (Enter e espaço), com rótulo.
- As animações respeitam `prefers-reduced-motion`.
