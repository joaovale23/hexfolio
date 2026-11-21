export interface Point { x: number; y: number }

/** Vértices de um hexágono com a ponta para cima, no sentido horário a partir do topo. */
export function hexagon(center: Point, radius: number): Point[] {
  return Array.from({ length: 6 }, (_, i) => {
    const angle = (Math.PI / 180) * (-90 + 60 * i)
    return { x: round(center.x + radius * Math.cos(angle)), y: round(center.y + radius * Math.sin(angle)) }
  })
}

export const toPoints = (points: Point[]) => points.map(p => `${p.x},${p.y}`).join(' ')

/** O lado i liga o vértice i ao vértice i+1. Lado 0 = em cima à direita; lado 5 = em cima à esquerda. */
export function edge(points: Point[], i: number): [Point, Point] {
  return [points[i], points[(i + 1) % 6]]
}

export const midpoint = (a: Point, b: Point): Point => ({ x: round((a.x + b.x) / 2), y: round((a.y + b.y) / 2) })

/** Trapézio entre o lado i do hexágono externo e o do interno: a faixa clicável de cada seção. */
export function band(outer: Point[], inner: Point[], i: number): Point[] {
  const [a, b] = edge(outer, i)
  const [c, d] = edge(inner, i)
  return [a, b, d, c]
}

/** Ponto a uma fração t (0..1) de uma linha quebrada. */
export function alongPath(path: Point[], t: number): Point {
  const lengths = path.slice(1).map((p, i) => Math.hypot(p.x - path[i].x, p.y - path[i].y))
  let remaining = t * lengths.reduce((a, b) => a + b, 0)
  for (let i = 0; i < lengths.length; i++) {
    if (remaining <= lengths[i]) {
      const f = lengths[i] === 0 ? 0 : remaining / lengths[i]
      return { x: round(path[i].x + (path[i + 1].x - path[i].x) * f), y: round(path[i].y + (path[i + 1].y - path[i].y) * f) }
    }
    remaining -= lengths[i]
  }
  return path[path.length - 1]
}

const round = (n: number) => Math.round(n * 10) / 10
