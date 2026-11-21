import { useState, type CSSProperties } from 'react'
import type { SectionId, Strings } from '../i18n'
import { SkillIcon, hasSkillIcon, skillColor } from './SkillIcon'
import type { Portfolio } from '../types'

interface Props { id: SectionId; portfolio: Portfolio; t: Strings }

/** O conteúdo de cada lado do hexágono. Contato tem componente próprio. */
export function SectionContent({ id, portfolio, t }: Props) {
  const { profile } = portfolio
  switch (id) {
    case 'about':
      return (
        <>
          <p className="lead">{profile.bio}</p>
          <ul className="chips">{profile.tags.map(tag => <li key={tag}>{tag}</li>)}</ul>
        </>
      )
    case 'experience':
      return (
        <ol className="timeline">
          {portfolio.experiences.map(e => (
            <li key={e.company}>
              <h3>{e.company}</h3>
              <p className="meta">{e.role} · {e.period} · {e.location}</p>
              <p>{e.description}</p>
              {e.highlights.length > 0 && <ul className="bullets">{e.highlights.map(h => <li key={h}>{h}</li>)}</ul>}
            </li>
          ))}
        </ol>
      )
    case 'projects':
      return (
        <div className="cards">
          {portfolio.projects.map(p => <ProjectCard key={p.slug} project={p} t={t} />)}
        </div>
      )
    case 'skills':
      return (
        <div className="skill-groups">
          {portfolio.skillGroups.map(g => (
            <section key={g.title}>
              <h3>{g.title}</h3>
              <ul className="skills">
                {g.skills.map(s => (
                  <li key={s} style={{ '--brand': skillColor(s) } as CSSProperties}>
                    <SkillIcon name={s} />
                    <span>{s}</span>
                  </li>
                ))}
              </ul>
            </section>
          ))}
        </div>
      )
    case 'education':
      return (
        <ul className="education">
          {portfolio.education.map(e => (
            <li key={e.course}>
              <h3>{e.course}</h3>
              <p className="meta">{e.institution}{e.period && ` · ${e.period}`}</p>
            </li>
          ))}
        </ul>
      )
    case 'contact':
      return null
  }
}

/** Card de projeto: os destaques ficam recolhidos para a lista caber na tela. */
function ProjectCard({ project: p, t }: { project: Portfolio['projects'][number]; t: Strings }) {
  const [open, setOpen] = useState(false)
  return (
    <article className="card">
      <header>
        <h3>{p.name}</h3>
        <span className="status">{p.status}</span>
      </header>
      <p>{p.description}</p>
      <ul className="chips small">
        {p.tags.map(tag => (
          <li key={tag} className={hasSkillIcon(tag) ? 'has-icon' : undefined}>
            {hasSkillIcon(tag) && <SkillIcon name={tag} />}{tag}
          </li>
        ))}
      </ul>
      {open && <ul className="bullets">{p.highlights.map(h => <li key={h}>{h}</li>)}</ul>}
      <footer>
        {p.highlights.length > 0 && (
          <button type="button" className="toggle" aria-expanded={open} onClick={() => setOpen(o => !o)}>
            {open ? t.lessHighlights : `${t.moreHighlights} (${p.highlights.length})`}
          </button>
        )}
        {p.privateCode
          ? <span className="private">{t.privateCode}</span>
          : <a href={p.repoUrl ?? '#'} target="_blank" rel="noreferrer">{t.repo} ↗</a>}
        {p.demoUrl && <a href={p.demoUrl} target="_blank" rel="noreferrer">{t.demo} ↗</a>}
      </footer>
    </article>
  )
}
