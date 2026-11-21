import { describe, expect, it } from 'vitest'
import snapshot from '../../public/snapshot/architecture.json'
import type { ArchitectureMap } from '../types'
import { layoutArchitecture, splitName } from './architectureLayout'
import { componentsLitBy } from './trace'

const map = snapshot as ArchitectureMap

describe('splitName', () => {
  it('mantém nomes curtos numa linha', () => {
    expect(splitName('Notifier')).toEqual(['Notifier'])
  })

  it('quebra nomes longos em duas linhas equilibradas', () => {
    expect(splitName('DescribeArchitectureService')).toEqual(['Describe', 'ArchitectureService'])
    expect(splitName('AnnotationArchitectureSource')).toEqual(['Annotation', 'ArchitectureSource'])
  })
})

describe('layoutArchitecture', () => {
  const { placed, columns, center, radius, view } = layoutArchitecture(map)

  it('posiciona todas as peças do mapa real', () => {
    expect(placed.size).toBe(map.components.length)
  })

  it('segue o fluxo da esquerda para a direita', () => {
    expect(placed.get('ContactController')!.x).toBe(columns.inboundAdapter)
    expect(placed.get('SubmitContact')!.x).toBe(columns.inboundPort)
    expect(placed.get('SubmitContactService')!.x).toBe(columns.useCase)
    expect(placed.get('ContactMessage')!.x).toBe(columns.domain)
    expect(placed.get('ContactQueue')!.x).toBe(columns.outboundPort)
    expect(placed.get('SqsContactQueue')!.x).toBe(columns.outboundAdapter)
  })

  it('deixa cada caso de uso na linha da porta que implementa', () => {
    expect(placed.get('ProcessContactService')!.y).toBe(placed.get('ProcessContact')!.y)
    expect(placed.get('GetPortfolioService')!.y).toBe(placed.get('GetPortfolio')!.y)
  })

  it('põe as portas sobre os lados verticais do hexágono', () => {
    const halfWidth = (radius * Math.sqrt(3)) / 2
    expect(columns.inboundPort).toBeCloseTo(center.x - halfWidth, -1)
    expect(columns.outboundPort).toBeCloseTo(center.x + halfWidth, -1)
  })

  it('não sobrepõe peças da mesma coluna nem sai do desenho', () => {
    const byColumn = new Map<number, { y: number; height: number }[]>()
    placed.forEach(p => {
      byColumn.set(p.x, [...(byColumn.get(p.x) ?? []), p])
      expect(p.x - p.width / 2).toBeGreaterThanOrEqual(0)
      expect(p.x + p.width / 2).toBeLessThanOrEqual(view.width)
    })
    byColumn.forEach(items => {
      const sorted = [...items].sort((a, b) => a.y - b.y)
      sorted.slice(1).forEach((item, i) => expect(item.y - item.height / 2).toBeGreaterThanOrEqual(sorted[i].y + sorted[i].height / 2))
    })
  })
})

describe('componentsLitBy', () => {
  it('acende a porta, quem a implementa e o adaptador de entrada que a aciona', () => {
    expect(componentsLitBy(map, 'SubmitContact')).toEqual(new Set(['SubmitContact', 'SubmitContactService', 'ContactController']))
  })

  it('acende os dois consumidores da fila no processamento', () => {
    expect(componentsLitBy(map, 'ProcessContact')).toEqual(
      new Set(['ProcessContact', 'ProcessContactService', 'SqsContactListener', 'SqsDeadLetterListener']))
  })

  it('acende o adaptador de saída da porta', () => {
    expect(componentsLitBy(map, 'Notifier')).toEqual(new Set(['Notifier', 'SmtpNotifier']))
  })
})
