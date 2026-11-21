import type { ReactNode } from 'react'
import type { Strings } from '../i18n'
import type { ArchitectureMap } from '../types'

interface Props { map: ArchitectureMap; id: string | null; t: Strings; extra?: ReactNode }

export function ComponentDetails({ map, id, t, extra }: Props) {
  const component = map.components.find(c => c.id === id)
  if (!component) return <p className="hint">{t.selectHint}</p>
  const uses = map.links.filter(l => l.from === component.id)
  const usedBy = map.links.filter(l => l.to === component.id)
  return (
    <div className={`details kind-${component.kind.toLowerCase()}`}>
      <p className="details-kind">{t.kinds[component.kind]}</p>
      <h3>{component.id}</h3>
      <p>{component.description}</p>
      {(uses.length > 0 || usedBy.length > 0) && (
        <ul className="details-links">
          {uses.map(l => <li key={`u-${l.to}`}>{l.relation === 'IMPLEMENTS' ? 'implements' : 'uses'} <b>{l.to}</b></li>)}
          {usedBy.map(l => <li key={`b-${l.from}`}><b>{l.from}</b> {l.relation === 'IMPLEMENTS' ? 'implements' : 'uses'} this</li>)}
        </ul>
      )}
      {extra}
      <a className="details-source" href={component.sourceUrl} target="_blank" rel="noreferrer">{t.openSource} ↗</a>
    </div>
  )
}
