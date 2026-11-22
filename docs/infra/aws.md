# Infra · filas na AWS

Em desenvolvimento e nos testes, as filas são criadas pelo `SqsQueueProvisioner` no LocalStack. Em produção, elas são criadas uma vez na conta AWS.

## 1. Filas (região `us-east-2`, a mesma da API no Render e do banco no Neon)

1. Criar a fila **`hexfolio-contact-dlq`** (Standard).
2. Criar a fila **`hexfolio-contact`** (Standard):
   - *Visibility timeout*: 30 s
   - *Receive message wait time*: 20 s (espera longa)
   - *Dead-letter queue*: `hexfolio-contact-dlq`, com *Maximum receives* = 3

## 2. Usuário IAM com acesso mínimo

Política que permite só o necessário, e só nessas duas filas:

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": ["sqs:SendMessage", "sqs:ReceiveMessage", "sqs:DeleteMessage",
               "sqs:ChangeMessageVisibility", "sqs:GetQueueUrl", "sqs:GetQueueAttributes"],
    "Resource": [
      "arn:aws:sqs:us-east-2:<conta>:hexfolio-contact",
      "arn:aws:sqs:us-east-2:<conta>:hexfolio-contact-dlq"
    ]
  }]
}
```

## 3. Alerta de gasto

Em *Billing → Budgets*, um orçamento mensal de **US$ 1** com alerta por e-mail. O uso esperado fica dentro do gratuito, então o alerta só dispara se algo sair do previsto.

## 4. Segredos no Render

`AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` do usuário IAM acima, preenchidos no painel do serviço `hexfolio-api` (ver `render.yaml`).
