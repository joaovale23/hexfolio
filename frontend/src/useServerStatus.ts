import { useEffect, useState } from 'react'
import { API_URL } from './api'

/** Horário em que um ping externo mantém a API acordada (fuso de São Paulo). Fora dele, ela dorme. */
export const AWAKE_HOURS = { from: 8, to: 19 }
/** Se a API responder antes disso, ninguém precisa ver aviso nenhum. */
const SHOW_AFTER_MS = 2500

export function isOffHours(now = new Date()): boolean {
  const hour = Number(new Intl.DateTimeFormat('en-US', { timeZone: 'America/Sao_Paulo', hour: 'numeric', hourCycle: 'h23' }).format(now))
  return hour < AWAKE_HOURS.from || hour >= AWAKE_HOURS.to
}

export type ServerState = 'checking' | 'waking' | 'ready' | 'up'

/**
 * Descobre se a API gratuita está dormindo. Enquanto ela acorda, devolve 'waking' e os segundos de espera;
 * quando responde, passa por 'ready' (para o aviso confirmar) e chega a 'up'.
 */
export function useServerStatus(): { state: ServerState; elapsed: number; woke: boolean } {
  const [state, setState] = useState<ServerState>(API_URL === null ? 'up' : 'checking')
  const [elapsed, setElapsed] = useState(0)
  // Verdadeiro depois que a API acordou durante a visita: sinal para recarregar o que veio da cópia estática.
  const [woke, setWoke] = useState(false)

  useEffect(() => {
    if (API_URL === null) return
    let cancelled = false
    let waking = false
    let ticker: ReturnType<typeof setInterval> | undefined
    const started = Date.now()
    const reveal = setTimeout(() => {
      waking = true
      setState('waking')
      ticker = setInterval(() => setElapsed(Math.floor((Date.now() - started) / 1000)), 1000)
    }, SHOW_AFTER_MS)

    void (async () => {
      // /api/architecture é estático e já tem CORS; enquanto o Render acorda, ele pode devolver erro, então tenta de novo.
      while (!cancelled) {
        try {
          const response = await fetch(`${API_URL}/api/architecture`, { cache: 'no-store' })
          if (response.ok) break
        } catch { /* rede ou CORS durante o boot */ }
        await new Promise(resolve => setTimeout(resolve, 4000))
      }
      if (cancelled) return
      clearTimeout(reveal)
      clearInterval(ticker)
      if (waking) {
        setState('ready')
        setWoke(true)
        setTimeout(() => { if (!cancelled) setState('up') }, 1800)
      } else {
        setState('up')
      }
    })()

    return () => { cancelled = true; clearTimeout(reveal); clearInterval(ticker) }
  }, [])

  return { state, elapsed, woke }
}
