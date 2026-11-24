import { describe, expect, it } from 'vitest'
import { isOffHours } from './useServerStatus'

describe('isOffHours', () => {
  it('usa o horário de São Paulo, não o do navegador', () => {
    expect(isOffHours(new Date('2026-11-23T11:00:00Z'))).toBe(false) // 8h
    expect(isOffHours(new Date('2026-11-23T21:59:00Z'))).toBe(false) // 18h59
  })

  it('fica fora do horário de 19h às 8h', () => {
    expect(isOffHours(new Date('2026-11-23T22:00:00Z'))).toBe(true) // 19h
    expect(isOffHours(new Date('2026-11-24T05:30:00Z'))).toBe(true) // 2h30
    expect(isOffHours(new Date('2026-11-23T10:59:00Z'))).toBe(true) // 7h59
  })
})
