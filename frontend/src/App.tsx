import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { loadArchitecture, loadPortfolio } from './api'
import { ApiPeek } from './components/ApiPeek'
import { ComponentDetails } from './components/ComponentDetails'
import { ContactForm } from './components/ContactForm'
import { DevHex } from './components/DevHex'
import { MiniHex, NormalHex } from './components/NormalHex'
import { SectionContent } from './components/Sections'
import { componentsLitBy, isFailure } from './hex/trace'
import { SECTIONS, STRINGS, type SectionId } from './i18n'
import type { ArchitectureMap, Lang, Loaded, Portfolio, TraceEvent } from './types'
import { useMediaQuery } from './useMediaQuery'

/** Tempo mínimo de cada etapa acesa: o SQS é mais rápido que o olho. */
const STEP_MS = 750

const initialLang = (): Lang => (navigator.language?.toLowerCase().startsWith('pt') ? 'pt' : 'en')
const initials = (name: string) => name.split(' ').filter(Boolean).map(w => w[0]).filter((_, i, all) => i === 0 || i === all.length - 1).join('')

export default function App() {
  const [lang, setLang] = useState<Lang>(initialLang)
  const [devMode, setDevMode] = useState(false)
  const [section, setSection] = useState<SectionId>('about')
  const [portfolio, setPortfolio] = useState<Loaded<Portfolio> | null>(null)
  const [architecture, setArchitecture] = useState<Loaded<ArchitectureMap> | null>(null)
  const [selected, setSelected] = useState<string | null>(null)
  const [events, setEvents] = useState<TraceEvent[]>([])
  const [current, setCurrent] = useState<TraceEvent | null>(null)
  const queue = useRef<TraceEvent[]>([])
  const playing = useRef(false)
  const isMobile = useMediaQuery('(max-width: 767px)')
  const t = STRINGS[lang]

  useEffect(() => { document.documentElement.lang = lang === 'pt' ? 'pt-BR' : 'en'; loadPortfolio(lang, setPortfolio) }, [lang])
  useEffect(() => { loadArchitecture(setArchitecture) }, [])

  // As etapas chegam pelo SSE quase juntas; tocamos uma de cada vez para dar para acompanhar.
  const playNext = useCallback(() => {
    const next = queue.current.shift()
    if (!next) {
      playing.current = false
      setTimeout(() => setCurrent(c => (c && (c.step === 'NOTIFIED' || c.step === 'FAILED') ? null : c)), 2500)
      return
    }
    playing.current = true
    setCurrent(next)
    setEvents(prev => [...prev, next])
    setTimeout(playNext, STEP_MS)
  }, [])

  const onTraceEvent = useCallback((event: TraceEvent) => {
    queue.current.push(event)
    if (!playing.current) playNext()
  }, [playNext])

  const onTraceStart = useCallback(() => { setEvents([]); setSelected(null); queue.current = [] }, [])

  const lit = useMemo(
    () => (architecture && current ? componentsLitBy(architecture.data, current.component) : new Set<string>()),
    [architecture, current])

  if (!portfolio) return <div className="loading" aria-busy="true" />
  const p = portfolio.data

  const contactForm = (
    <ContactForm t={t} devMode={devMode} events={events} onTraceEvent={onTraceEvent} onTraceStart={onTraceStart} />
  )

  const sectionPanel = (id: SectionId) => (
    <section key={id} className={`panel panel-${id}`} aria-labelledby={`h-${id}`}>
      <h2 id={`h-${id}`}>{t.sections[id]}</h2>
      {id === 'contact' ? contactForm : <SectionContent id={id} portfolio={p} t={t} />}
    </section>
  )

  /** Controllers que já responderam algo nesta página ganham a requisição real nos detalhes. */
  const peekFor = (id: string | null) => {
    if (id === 'PortfolioController') return <ApiPeek loaded={portfolio} slice={p} t={t} />
    if (id === 'ArchitectureController' && architecture) return <ApiPeek loaded={architecture} slice={architecture.data} t={t} />
    return undefined
  }

  return (
    <div className={`app${devMode ? ' is-dev' : ''}`}>
      <header className="topbar">
        <a className="brand" href="#top" aria-label="hexfolio">
          <svg viewBox="0 0 24 24" aria-hidden="true"><polygon points="12,2 21,7 21,17 12,22 3,17 3,7" /></svg>
          hexfolio
        </a>
        <div className="controls">
          <button type="button" className="lang" onClick={() => setLang(l => (l === 'pt' ? 'en' : 'pt'))} aria-label="language">
            {lang === 'pt' ? 'EN' : 'PT'}
          </button>
          <label className="switch">
            <input type="checkbox" role="switch" checked={devMode} onChange={e => { setDevMode(e.target.checked); setSelected(null) }} />
            <span className="switch-track" aria-hidden="true" />
            <span>{t.devMode}</span>
          </label>
        </div>
      </header>

      {isMobile ? (
        <main className="mobile">
          <div className="hero">
            <MiniHex />
            <p className="eyebrow">{p.profile.role}</p>
            <h1>{p.profile.name}</h1>
            <p className="tagline">{t.tagline}</p>
          </div>
          {SECTIONS.map(sectionPanel)}
        </main>
      ) : devMode && architecture ? (
        <main className="dev">
          <div className="dev-stage">
            <p className="dev-hint">{t.devHintOn}</p>
            <DevHex map={architecture.data} selected={selected} lit={lit} failing={current ? isFailure(current.step) : false}
                    onSelect={setSelected} t={t} />
          </div>
          <aside className="dev-side">
            <section className="dev-card">
              <h2>{t.piece}</h2>
              <ComponentDetails map={architecture.data} id={selected} t={t} extra={peekFor(selected)} />
            </section>
            <section className="dev-card">
              <h2>{t.liveTest}</h2>
              <p className="hint">{t.liveTestHint}</p>
              {contactForm}
            </section>
          </aside>
        </main>
      ) : (
        <main className="normal">
          <div className="normal-text">
            <div className="hero">
              <p className="eyebrow">{p.profile.role}</p>
              <h1>{p.profile.name}</h1>
              <p className="tagline">{t.tagline}</p>
              <p className="meta">{p.profile.location} · <span className="ok">{p.profile.status}</span></p>
            </div>
            {sectionPanel(section)}
          </div>
          <div className="normal-hex">
            <NormalHex initials={initials(p.profile.name)} active={section} onSelect={setSection} t={t} />
            <button type="button" className="dev-cta" onClick={() => setDevMode(true)}>{t.devCta} →</button>
          </div>
        </main>
      )}
    </div>
  )
}
