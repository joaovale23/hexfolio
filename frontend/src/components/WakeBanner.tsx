import type { Strings } from '../i18n'
import { AWAKE_HOURS, isOffHours, type ServerState } from '../useServerStatus'

/** Quanto a API gratuita do Render costuma levar para ligar depois de dormir. */
const EXPECTED_WAKE_SECONDS = 60

interface Props { state: ServerState; elapsed: number; t: Strings }

/** Aviso fixo enquanto a API acorda. A página já está completa pela cópia estática; só o que é ao vivo espera. */
export function WakeBanner({ state, elapsed, t }: Props) {
  if (state !== 'waking' && state !== 'ready') return null
  const offHours = isOffHours()
  const ready = state === 'ready'
  const progress = ready ? 100 : Math.min(95, Math.round((elapsed / EXPECTED_WAKE_SECONDS) * 100))
  const w = t.wake
  return (
    <aside className={`wake${ready ? ' is-ready' : ''}`} role="status" aria-live="polite">
      <span className="wake-dot" aria-hidden="true" />
      <div className="wake-text">
        <strong>{ready ? w.ready : offHours ? w.titleOff : w.titleOn}</strong>
        {!ready && (
          <span>
            {offHours ? w.bodyOff(AWAKE_HOURS.from, AWAKE_HOURS.to) : w.bodyOn} {w.meanwhile}
            {elapsed > EXPECTED_WAKE_SECONDS * 2 && <> {w.slow}</>}
          </span>
        )}
        <span className="wake-bar" aria-hidden="true"><i style={{ width: `${progress}%` }} /></span>
      </div>
      {!ready && <span className="wake-timer" aria-label={w.timer}>{elapsed}s</span>}
    </aside>
  )
}
