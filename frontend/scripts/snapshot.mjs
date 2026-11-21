// Gera a cópia estática que a página mostra enquanto a API acorda (spec 05).
// Uso: com a API rodando, `npm run snapshot` (ou API_URL=https://... npm run snapshot).
import { writeFile } from 'node:fs/promises'

const api = process.env.API_URL ?? 'http://localhost:8080'
const files = {
  'portfolio.pt.json': '/api/portfolio?lang=pt',
  'portfolio.en.json': '/api/portfolio?lang=en',
  'architecture.json': '/api/architecture',
}

for (const [file, path] of Object.entries(files)) {
  const response = await fetch(api + path)
  if (!response.ok) throw new Error(`${response.status} em ${path}`)
  await writeFile(new URL(`../public/snapshot/${file}`, import.meta.url), JSON.stringify(await response.json(), null, 2) + '\n')
  console.log(`✓ ${file}`)
}
