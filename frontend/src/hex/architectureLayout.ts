import type { ArchComponent, ArchitectureMap, Kind } from '../types'
import type { Point } from './geometry'

/** Tamanho da fonte das peças, em unidades do viewBox. Tudo abaixo é calculado a partir dele. */
export const FONT = 12.5
const CHAR = FONT * 0.6
const LINE = FONT * 1.25
const PAD = 10

/** Quebra um nome CamelCase em até duas linhas o mais equilibradas possível. */
export function splitName(name: string): string[] {
  const words = name.match(/[A-Z][a-z0-9]*|[a-z0-9]+/g) ?? [name]
  if (words.length < 2 || name.length <= 14) return [name]
  let best: string[] = [name]
  let bestMax = Infinity
  for (let i = 1; i < words.length; i++) {
    const lines = [words.slice(0, i).join(''), words.slice(i).join('')]
    const max = Math.max(...lines.map(l => l.length))
    if (max < bestMax) { best = lines; bestMax = max }
  }
  return best
}

/** Modelos de domínio têm nomes curtos e ficam empilhados: uma linha só, para o hexágono central caber. */
const linesOf = (name: string, kind?: Kind) => (kind === 'DOMAIN_MODEL' ? [name] : splitName(name))
export const nodeWidth = (name: string, kind?: Kind) => Math.ceil(Math.max(...linesOf(name, kind).map(l => l.length)) * CHAR + PAD * 2)
export const nodeHeight = (name: string, kind?: Kind) => Math.ceil(linesOf(name, kind).length * LINE + PAD)

const COLUMN_GAP = 26

export interface Layout {
  view: { width: number; height: number }
  center: Point
  /** Raio do hexágono da aplicação: as portas ficam sobre os lados verticais. */
  radius: number
  domainRadius: number
  columns: Record<'inboundAdapter' | 'inboundPort' | 'useCase' | 'domain' | 'outboundPort' | 'outboundAdapter', number>
  placed: Map<string, Placed>
}

export interface Placed { component: ArchComponent; x: number; y: number; width: number; height: number; lines: string[] }

/**
 * Colunas na ordem em que uma requisição atravessa o sistema:
 * adaptador de entrada → porta de entrada → caso de uso → domínio → porta de saída → adaptador de saída.
 * As larguras saem dos nomes reais, então o desenho se ajusta sozinho quando surgem peças novas.
 */
export function layoutArchitecture(map: ArchitectureMap): Layout {
  const of = (kind: Kind) => map.components.filter(c => c.kind === kind)
  const widest = (kind: Kind) => Math.max(80, ...of(kind).map(c => nodeWidth(c.id, kind)))
  const linkTo = (from: string, relation: 'IMPLEMENTS' | 'USES') => map.links.find(l => l.from === from && l.relation === relation)?.to

  const w = {
    inA: widest('INBOUND_ADAPTER'), inP: widest('INBOUND_PORT'), uc: widest('USE_CASE'),
    dom: widest('DOMAIN_MODEL'), outP: widest('OUTBOUND_PORT'), outA: widest('OUTBOUND_ADAPTER'),
  }
  const margin = 16
  const inboundAdapter = margin + w.inA / 2
  const inboundPort = inboundAdapter + w.inA / 2 + COLUMN_GAP + w.inP / 2
  const useCase = inboundPort + w.inP / 2 + COLUMN_GAP - 6 + w.uc / 2
  const domain = useCase + w.uc / 2 + COLUMN_GAP + 18 + w.dom / 2 // folga para o hexágono do domínio
  const halfWidth = domain - inboundPort
  const outboundPort = domain + halfWidth
  const outboundAdapter = outboundPort + w.outP / 2 + COLUMN_GAP + w.outA / 2
  const width = Math.ceil(outboundAdapter + w.outA / 2 + margin)

  const radius = Math.round(halfWidth / (Math.sqrt(3) / 2))
  const center = { x: domain, y: radius + 40 }
  const height = Math.ceil(center.y + radius + 24)
  const columns = { inboundAdapter, inboundPort, useCase, domain, outboundPort, outboundAdapter }

  const span = radius * 0.86 // dentro da parte reta dos lados verticais
  const rows = (count: number, s = span) =>
    count <= 1 ? [center.y] : Array.from({ length: count }, (_, i) => Math.round(center.y - s / 2 + (s * i) / (count - 1)))

  const placed = new Map<string, Placed>()
  const put = (component: ArchComponent, x: number, y: number) => {
    const lines = linesOf(component.id, component.kind)
    placed.set(component.id, { component, x, y, width: w[keyOf(component.kind)], height: nodeHeight(component.id, component.kind), lines })
  }

  const inboundPorts = sortByPreference(of('INBOUND_PORT'), ['GetPortfolio', 'DescribeArchitecture', 'SubmitContact', 'TraceContact', 'ProcessContact'])
  const inY = rows(inboundPorts.length)
  inboundPorts.forEach((p, i) => put(p, inboundPort, inY[i]))

  of('USE_CASE').forEach((uc, i) => put(uc, useCase, placed.get(linkTo(uc.id, 'IMPLEMENTS') ?? '')?.y ?? inY[i % inY.length]))

  const firstUserY = (id: string) => {
    const ys = map.links.filter(l => l.to === id && l.relation === 'USES').map(l => placed.get(l.from)?.y ?? Infinity)
    return ys.length ? Math.min(...ys) : Infinity
  }
  const outboundPorts = [...of('OUTBOUND_PORT')].sort((a, b) => firstUserY(a.id) - firstUserY(b.id))
  const outY = rows(outboundPorts.length)
  outboundPorts.forEach((p, i) => put(p, outboundPort, outY[i]))

  const models = of('DOMAIN_MODEL')
  const modelHeight = Math.max(nodeHeight('X'), ...models.map(m => nodeHeight(m.id, 'DOMAIN_MODEL')))
  const modelStep = modelHeight + 4
  models.forEach((m, i) => put(m, domain, center.y - ((models.length - 1) * modelStep) / 2 + i * modelStep))

  const yOf = (id: string | undefined) => (id ? placed.get(id)?.y ?? Infinity : Infinity)
  const inboundAdapters = [...of('INBOUND_ADAPTER')].sort((a, b) => yOf(linkTo(a.id, 'USES')) - yOf(linkTo(b.id, 'USES')) || a.id.localeCompare(b.id))
  const adapterHeight = Math.max(...inboundAdapters.map(a => nodeHeight(a.id)))
  rows(inboundAdapters.length, Math.max(span + 60, (inboundAdapters.length - 1) * (adapterHeight + 10))).forEach((y, i) => put(inboundAdapters[i], inboundAdapter, y))
  of('OUTBOUND_ADAPTER').forEach((a, i) => {
    const y = yOf(linkTo(a.id, 'IMPLEMENTS'))
    put(a, outboundAdapter, y === Infinity ? outY[i % outY.length] : y)
  })

  // O hexágono do domínio precisa conter a pilha de modelos: seus lados verticais vão de -r/2 a +r/2.
  const stackHalf = ((models.length - 1) * modelStep) / 2 + modelHeight / 2
  const domainRadius = Math.round(Math.max(2 * stackHalf + 4, w.dom / Math.sqrt(3) + 8))
  return { view: { width, height }, center, radius, domainRadius, columns, placed }
}

function keyOf(kind: Kind): 'inA' | 'inP' | 'uc' | 'dom' | 'outP' | 'outA' {
  return ({ INBOUND_ADAPTER: 'inA', INBOUND_PORT: 'inP', USE_CASE: 'uc', DOMAIN_MODEL: 'dom', OUTBOUND_PORT: 'outP', OUTBOUND_ADAPTER: 'outA' } as const)[kind]
}

/** Ordena pelos nomes preferidos; peças novas, que ainda não estão na lista, vão para o fim. */
function sortByPreference(items: ArchComponent[], preferred: string[]): ArchComponent[] {
  const rank = (id: string) => (preferred.includes(id) ? preferred.indexOf(id) : preferred.length)
  return [...items].sort((a, b) => rank(a.id) - rank(b.id) || a.id.localeCompare(b.id))
}
