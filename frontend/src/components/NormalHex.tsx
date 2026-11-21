import { SECTIONS, type SectionId, type Strings } from '../i18n'
import { hexagon, toPoints, type Point } from '../hex/geometry'

const C: Point = { x: 260, y: 260 }
const R = 236
const CORE = 66
const OUTER = hexagon(C, R)
const INNER = hexagon(C, CORE)

/** Cada seção é uma fatia do hexágono: do lado interno ao externo, entre dois vértices consecutivos. */
const SLICE_OF: Record<SectionId, number> = { about: 5, experience: 0, projects: 1, skills: 2, education: 3, contact: 4 }

function slice(i: number): Point[] {
  const j = (i + 1) % 6
  return [INNER[i], OUTER[i], OUTER[j], INNER[j]]
}

/** Centro da fatia, empurrado um pouco para fora para o texto ficar no meio da área colorida. */
function sliceCenter(i: number): Point {
  const pts = slice(i)
  return { x: pts.reduce((a, p) => a + p.x, 0) / 4, y: pts.reduce((a, p) => a + p.y, 0) / 4 }
}

/** Direção para fora da fatia: a fatia ativa "salta" nessa direção. */
function outward(i: number): Point {
  const c = sliceCenter(i)
  const len = Math.hypot(c.x - C.x, c.y - C.y)
  return { x: ((c.x - C.x) / len) * 12, y: ((c.y - C.y) / len) * 12 }
}

interface Props { initials: string; active: SectionId; onSelect: (id: SectionId) => void; t: Strings }

/** Modo normal: seis fatias, seis seções. */
export function NormalHex({ initials, active, onSelect, t }: Props) {
  return (
    <svg className="hex hex-normal" viewBox="0 0 520 520" role="group" aria-label="Navegação do portfólio">
      {SECTIONS.map(id => {
        const i = SLICE_OF[id]
        const center = sliceCenter(i)
        const shift = outward(i)
        const isActive = active === id
        return (
          <g key={id}
             className={`slice slice-${id}${isActive ? ' is-active' : ''}`}
             style={isActive ? { transform: `translate(${shift.x}px, ${shift.y}px)` } : undefined}
             role="button" tabIndex={0} aria-pressed={isActive} aria-label={t.sections[id]}
             onClick={() => onSelect(id)}
             onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); onSelect(id) } }}>
            <polygon points={toPoints(slice(i))} />
            <text x={center.x} y={center.y} textAnchor="middle" dominantBaseline="central">{t.sections[id]}</text>
          </g>
        )
      })}
      <polygon className="slice-core" points={toPoints(INNER)} />
      <text className="slice-initials" x={C.x} y={C.y} textAnchor="middle" dominantBaseline="central">{initials}</text>
    </svg>
  )
}

/** Versão pequena e decorativa, para o topo no celular. */
export function MiniHex() {
  return (
    <svg className="mini-hex" viewBox="0 0 520 520" aria-hidden="true">
      {SECTIONS.map(id => (
        <polygon key={id} className={`slice-${id}`} points={toPoints(slice(SLICE_OF[id]))} />
      ))}
    </svg>
  )
}
