import type { ArchitectureMap, Lang, Loaded, Portfolio, TraceEvent } from './types'

/**
 * Endereço da API. Em desenvolvimento, a local; no build, vem de VITE_API_URL.
 * Sem ele, o site publicado funciona só com a cópia estática e o contato aponta para o LinkedIn.
 */
export const API_URL: string | null =
  (import.meta.env.VITE_API_URL as string | undefined) || (import.meta.env.DEV ? 'http://localhost:8080' : null)
export const HAS_API = API_URL !== null

const snapshotUrl = (file: string) => `${import.meta.env.BASE_URL}snapshot/${file}`

async function timedFetch<T>(path: string, timeoutMs: number): Promise<Loaded<T>> {
  if (API_URL === null) throw new Error('API não configurada')
  const started = performance.now()
  const response = await fetch(API_URL + path, { signal: AbortSignal.timeout(timeoutMs) })
  if (!response.ok) throw new Error(`${response.status} em ${path}`)
  const data = (await response.json()) as T
  return { data, source: 'api', request: `GET ${path}`, status: response.status, ms: Math.round(performance.now() - started) }
}

async function snapshot<T>(file: string, request: string): Promise<Loaded<T>> {
  const response = await fetch(snapshotUrl(file))
  return { data: (await response.json()) as T, source: 'snapshot', request }
}

/**
 * Mostra a cópia estática na hora e troca pelos dados da API quando eles chegarem.
 * Se a API estiver dormindo (Fly.io), a página continua completa.
 */
export function loadWithFallback<T>(path: string, file: string, onData: (loaded: Loaded<T>) => void, timeoutMs = 20000) {
  let fromApi = false
  snapshot<T>(file, `GET ${path}`).then(s => { if (!fromApi) onData(s) }).catch(() => undefined)
  timedFetch<T>(path, timeoutMs).then(live => { fromApi = true; onData(live) }).catch(() => undefined)
}

export const loadPortfolio = (lang: Lang, onData: (l: Loaded<Portfolio>) => void) =>
  loadWithFallback<Portfolio>(`/api/portfolio?lang=${lang}`, `portfolio.${lang}.json`, onData)

export const loadArchitecture = (onData: (l: Loaded<ArchitectureMap>) => void) =>
  loadWithFallback<ArchitectureMap>('/api/architecture', 'architecture.json', onData)

export interface ContactInput { name: string; email: string; message: string; website: string }

export type SubmitResult =
  | { ok: true; id: string; traceUrl: string | null }
  | { ok: false; status: number; title: string; errors: Record<string, string> }

export async function submitContact(input: ContactInput): Promise<SubmitResult> {
  if (API_URL === null) return { ok: false, status: 0, title: 'offline', errors: {} }
  try {
    const response = await fetch(`${API_URL}/api/contact`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input),
    })
    const body = await response.json().catch(() => ({}))
    if (response.status === 202) return { ok: true, id: body.id, traceUrl: body.traceUrl ?? null }
    return { ok: false, status: response.status, title: body.title ?? 'Erro', errors: body.errors ?? {} }
  } catch {
    return { ok: false, status: 0, title: 'offline', errors: {} }
  }
}

/** Abre o SSE de trace e entrega cada etapa. Devolve a função que fecha a conexão. */
export function followTrace(traceUrl: string, onEvent: (event: TraceEvent) => void, onEnd: () => void): () => void {
  if (API_URL === null) { onEnd(); return () => undefined }
  const source = new EventSource(API_URL + traceUrl)
  source.addEventListener('step', e => {
    const event = JSON.parse((e as MessageEvent).data) as TraceEvent
    onEvent(event)
    if (event.step === 'NOTIFIED' || event.step === 'FAILED') { source.close(); onEnd() }
  })
  source.addEventListener('timeout', () => { source.close(); onEnd() })
  source.onerror = () => { source.close(); onEnd() }
  return () => source.close()
}
