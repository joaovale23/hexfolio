import { describe, expect, it } from 'vitest'
import { alongPath, band, hexagon } from './geometry'

describe('hexagon', () => {
  it('tem seis vértices, com a ponta para cima', () => {
    const points = hexagon({ x: 0, y: 0 }, 100)
    expect(points).toHaveLength(6)
    expect(points[0]).toEqual({ x: 0, y: -100 })
    expect(points[3]).toEqual({ x: 0, y: 100 })
  })

  it('todos os vértices ficam à mesma distância do centro', () => {
    hexagon({ x: 50, y: 50 }, 80).forEach(p => expect(Math.hypot(p.x - 50, p.y - 50)).toBeCloseTo(80, 0))
  })
})

describe('band', () => {
  it('liga o lado externo ao interno, formando um trapézio', () => {
    const outer = hexagon({ x: 0, y: 0 }, 100)
    const inner = hexagon({ x: 0, y: 0 }, 50)
    expect(band(outer, inner, 0)).toEqual([outer[0], outer[1], inner[1], inner[0]])
  })
})

describe('alongPath', () => {
  const path = [{ x: 0, y: 0 }, { x: 10, y: 0 }, { x: 10, y: 10 }]
  it('anda proporcionalmente ao comprimento', () => {
    expect(alongPath(path, 0)).toEqual({ x: 0, y: 0 })
    expect(alongPath(path, 0.5)).toEqual({ x: 10, y: 0 })
    expect(alongPath(path, 0.75)).toEqual({ x: 10, y: 5 })
    expect(alongPath(path, 1)).toEqual({ x: 10, y: 10 })
  })
})
