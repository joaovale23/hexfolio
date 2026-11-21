import { useMemo } from 'react'
import type { Strings } from '../i18n'
import { FONT, layoutArchitecture } from '../hex/architectureLayout'
import { hexagon, toPoints } from '../hex/geometry'
import type { ArchitectureMap } from '../types'

interface Props {
  map: ArchitectureMap
  selected: string | null
  lit: Set<string>
  failing: boolean
  onSelect: (id: string | null) => void
  t: Strings
}

/** Modo dev: o hexágono como planta técnica do backend que serve a página. */
export function DevHex({ map, selected, lit, failing, onSelect, t }: Props) {
  const layout = useMemo(() => layoutArchitecture(map), [map])
  const { view, center, radius, domainRadius, placed } = layout

  const focus = useMemo(() => {
    if (!selected) return new Set<string>()
    const ids = new Set([selected])
    map.links.forEach(l => { if (l.from === selected) ids.add(l.to); if (l.to === selected) ids.add(l.from) })
    return ids
  }, [map, selected])

  const dimmed = (id: string) => (selected !== null && !focus.has(id)) || (lit.size > 0 && !lit.has(id))
  const labelY = center.y - radius + 6

  return (
    <svg className="hex hex-dev" viewBox={`0 0 ${view.width} ${view.height}`} role="group"
         aria-label="Arquitetura hexagonal do hexfolio" onClick={() => onSelect(null)}>
      <polygon className="ring-app" points={toPoints(hexagon(center, radius))} />
      <polygon className="ring-domain" points={toPoints(hexagon(center, domainRadius))} />
      <text className="layer-label" x={8} y={labelY} textAnchor="start">{t.layers.inbound}</text>
      <text className="layer-label" x={view.width - 8} y={labelY} textAnchor="end">{t.layers.outbound}</text>
      <text className="layer-label" x={center.x} y={center.y - radius + 46} textAnchor="middle">{t.layers.core}</text>
      <text className="layer-label" x={center.x} y={center.y - domainRadius - 10} textAnchor="middle">{t.layers.domain}</text>

      <g className="links">
        {map.links.map(link => {
          const from = placed.get(link.from)
          const to = placed.get(link.to)
          if (!from || !to) return null
          const on = (lit.has(link.from) && lit.has(link.to)) || (selected !== null && (link.from === selected || link.to === selected))
          return (
            <line key={`${link.from}-${link.to}`} x1={from.x} y1={from.y} x2={to.x} y2={to.y}
                  className={`link link-${link.relation.toLowerCase()}${on ? ' is-on' : ''}${on && failing ? ' is-failing' : ''}`} />
          )
        })}
      </g>

      {[...placed.values()].map(({ component, x, y, width, height, lines }) => {
        const isLit = lit.has(component.id)
        const classes = [
          'node', `kind-${component.kind.toLowerCase()}`,
          selected === component.id && 'is-selected', isLit && 'is-lit', isLit && failing && 'is-failing', dimmed(component.id) && 'is-dim',
        ].filter(Boolean).join(' ')
        return (
          <g key={component.id} className={classes} transform={`translate(${x} ${y})`}
             role="button" tabIndex={0} aria-label={`${t.kinds[component.kind]}: ${component.id}`}
             onClick={e => { e.stopPropagation(); onSelect(component.id) }}
             onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); onSelect(component.id) } }}>
            <rect x={-width / 2} y={-height / 2} width={width} height={height} rx={component.kind.endsWith('PORT') ? height / 2 : 4} />
            <text textAnchor="middle" fontSize={FONT}>
              {lines.map((line, i) => (
                <tspan key={line} x={0} dy={i === 0 ? `${-(lines.length - 1) * 0.62 + 0.35}em` : '1.25em'}>{line}</tspan>
              ))}
            </text>
          </g>
        )
      })}
    </svg>
  )
}
