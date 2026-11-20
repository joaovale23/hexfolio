# Infra · filas na AWS

Em desenvolvimento e nos testes, as filas são criadas pelo `SqsQueueProvisioner` no LocalStack. Em produção, elas são criadas uma vez na conta AWS.

## 1. Filas (região `sa-east-1`)

1. Criar a fila **`hexfolio-contact-dlq`** (Standard).
2. Criar a fila **`hexfolio-contact`** (Standard):
   - *Visibility timeout*: 30 s
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
      "arn:aws:sqs:sa-east-1:<conta>:hexfolio-contact",
      "arn:aws:sqs:sa-east-1:<conta>:hexfolio-contact-dlq"
    ]
  }]
}
```

## 3. Segredos no Fly.io

```bash
fly secrets set AWS_ACCESS_KEY_ID=... AWS_SECRET_ACCESS_KEY=... AWS_REGION=sa-east-1
```
