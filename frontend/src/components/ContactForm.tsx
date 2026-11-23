import { useState, type FormEvent } from 'react'
import { HAS_API, followTrace, submitContact, type ContactInput } from '../api'
import type { Strings } from '../i18n'
import type { TraceEvent } from '../types'

interface Props {
  t: Strings
  devMode: boolean
  onTraceEvent: (event: TraceEvent) => void
  onTraceStart: () => void
  events: TraceEvent[]
}

type State = 'idle' | 'sending' | 'sent' | 'offline' | 'tooMany' | 'invalid'

const EMPTY: ContactInput = { name: '', email: '', message: '', website: '' }

/** Canais diretos: quem recruta costuma preferir um link a um formulário. */
const CHANNELS = [
  { label: 'LinkedIn', href: 'https://www.linkedin.com/in/joaovitorvale' },
  { label: 'GitHub', href: 'https://github.com/joaovale23' },
  { label: 'joaovitorvalec@gmail.com', href: 'mailto:joaovitorvalec@gmail.com' },
]

function DirectLinks() {
  return (
    <ul className="contact-links">
      {CHANNELS.map(c => (
        <li key={c.label}>
          <a className="contact-link" href={c.href} target={c.href.startsWith('http') ? '_blank' : undefined} rel="noreferrer">
            {c.label}{c.href.startsWith('http') && ' ↗'}
          </a>
        </li>
      ))}
    </ul>
  )
}

export function ContactForm({ t, devMode, onTraceEvent, onTraceStart, events }: Props) {
  const [input, setInput] = useState<ContactInput>(EMPTY)
  const [state, setState] = useState<State>('idle')
  const [errors, setErrors] = useState<Record<string, string>>({})

  async function submit(e: FormEvent) {
    e.preventDefault()
    setState('sending')
    setErrors({})
    const result = await submitContact(input)
    if (result.ok) {
      setState('sent')
      setInput(EMPTY)
      if (result.traceUrl) {
        onTraceStart()
        followTrace(result.traceUrl, onTraceEvent, () => undefined)
      }
      return
    }
    if (result.status === 0 || result.status >= 500) setState('offline')
    else if (result.status === 429) setState('tooMany')
    else { setState('invalid'); setErrors(result.errors) }
  }

  const field = (key: keyof ContactInput) => ({
    value: input[key],
    onChange: (e: { target: { value: string } }) => setInput(prev => ({ ...prev, [key]: e.target.value })),
    'aria-invalid': Boolean(errors[key]),
  })

  if (!HAS_API) {
    return (
      <div className="contact">
        <p>{t.contactSoon}</p>
        <DirectLinks />
      </div>
    )
  }

  return (
    <div className="contact">
      {state !== 'sent' && <form onSubmit={submit} noValidate>
        <label>{t.name}<input name="name" autoComplete="name" {...field('name')} />{errors.name && <small>{errors.name}</small>}</label>
        <label>{t.email}<input name="email" type="email" autoComplete="email" {...field('email')} />{errors.email && <small>{errors.email}</small>}</label>
        <label>{t.message}<textarea name="message" rows={5} {...field('message')} />{errors.message && <small>{errors.message}</small>}</label>
        {/* Campo isca: invisível para pessoas, preenchido por robôs. */}
        <input className="honeypot" name="website" tabIndex={-1} autoComplete="off" aria-hidden="true" {...field('website')} />
        <button type="submit" disabled={state === 'sending'}>{state === 'sending' ? t.sending : t.send}</button>
      </form>}
      {state === 'sent' && (
        <p className="ok sent">{t.sent} <button type="button" className="link-button" onClick={() => setState('idle')}>{t.sendAnother}</button></p>
      )}
      {state === 'tooMany' && <p className="warn">{t.tooMany}</p>}
      {state === 'offline' && (
        <p className="warn">{t.offline} <a href="https://www.linkedin.com/in/joaovitorvale" target="_blank" rel="noreferrer">LinkedIn ↗</a></p>
      )}
      {!devMode && <p className="hint">{t.watchLive}</p>}
      {!devMode && <div className="direct"><p className="hint">{t.directContact}</p><DirectLinks /></div>}
      {events.length > 0 && (
        <ol className="trace" aria-live="polite">
          {events.map((e, i) => (
            <li key={i} className={e.step === 'ATTEMPT_FAILED' || e.step === 'FAILED' ? 'is-failing' : ''}>
              <span className="trace-step">{t.steps[e.step]}</span>
              <code>{e.component}</code>
              <span className="trace-ms">{e.tookMs} ms</span>
            </li>
          ))}
        </ol>
      )}
    </div>
  )
}
