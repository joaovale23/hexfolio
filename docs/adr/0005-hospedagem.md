# ADR 0005 · Hospedagem gratuita

**Status:** aceita · 2026-11-22 (substitui a proposta de 2026-11-20)

## Contexto

O objetivo é custo zero. Desde a proposta original, as contas novas no Fly.io perderam a cota gratuita, e o Koyeb encerrou o plano grátis para contas novas. A Oracle Cloud tem uma máquina gratuita sempre ligada, mas com riscos: a cota foi reduzida em 2026, máquinas ociosas podem ser recolhidas e há relatos de falta de capacidade em São Paulo.

## Decisão

| Peça | Escolha | Por quê |
|---|---|---|
| Front | GitHub Pages | Gratuito, deploy pelo GitHub Actions |
| API | **Render, plano Free**, região Ohio | Gratuito, sem cartão, roda a imagem Docker do repositório (`render.yaml`) |
| Manter a API acordada | Ping externo a cada 10 min (cron-job.org) em `GET /api/portfolio` | O plano Free dorme após 15 min sem tráfego. Uma única API ligada o mês todo (744 h) cabe nas 750 h gratuitas |
| Banco | **Neon, plano Free**, AWS us-east-2 | O Postgres gratuito do Render expira em 30 dias. O Neon fica na mesma região da API |
| Fila | AWS SQS, us-east-2 | [ADR 0002](0002-sqs-para-contato.md). 1 milhão de requisições/mês gratuitas |
| E-mail | Brevo (SMTP), plano Free | 300 e-mails/dia, sem cartão |

**Região:** tudo em Ohio (us-east-2). O plano gratuito da AWS para contas novas libera uma única região, e a da conta é Ohio. O Render e o Neon também têm Ohio, então API, banco e fila ficam juntos.

## Ajustes para a máquina gratuita (512 MB, 0,1 CPU)

Medidos num container com os mesmos limites:

- **Memória:** cerca de 310 MB com `-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k`.
- **Inicialização:** cerca de 2 min 30 s. Só acontece em deploys e reinícios.
- **Primeira requisição:** caía de 12,5 s para 3 s com o aquecimento na subida (`WarmUpConfiguration`) e o `DispatcherServlet` inicializado no startup.
- **SMTP:** o plano gratuito do Render bloqueia a saída pelas portas 25, 465 e 587, então a API usa a porta 2525 da Brevo, com tempo limite de 10 s.
- **SQS:** espera longa de 20 s por consulta, para manter o consumo de requisições bem abaixo da cota gratuita.

## Consequências

- Custo: R$ 0 por mês. Só a AWS exige cartão, com um alerta de gasto de US$ 1.
- Se o ping parar ou o Render reiniciar o serviço, a página continua completa pela cópia estática ([05-frontend](../spec/05-frontend.md)), e só o teste ao vivo espera a API acordar.
- Trocar de hospedagem não mexe no código: a imagem Docker e as variáveis de ambiente são as mesmas.
