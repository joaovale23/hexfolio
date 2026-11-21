import { describe, expect, it } from 'vitest'
import en from '../../public/snapshot/portfolio.en.json'
import pt from '../../public/snapshot/portfolio.pt.json'
import { hasSkillIcon, normalizeSkill, skillColor } from './SkillIcon'

describe('ícones de habilidades', () => {
  it('toda habilidade do portfólio tem ícone, em português e em inglês', () => {
    const semIcone = [pt, en].flatMap(p => p.skillGroups.flatMap(g => g.skills)).filter(s => !hasSkillIcon(s))
    expect(semIcone).toEqual([])
  })

  it('reconhece tags de projeto com versão', () => {
    expect(normalizeSkill('Java 21')).toBe('java')
    expect(normalizeSkill('Spring Boot 3')).toBe('spring boot')
    expect(hasSkillIcon('Java 25')).toBe(true)
  })

  it('usa a cor da marca', () => {
    expect(skillColor('Spring Boot')).toBe('#6DB33F')
    expect(skillColor('PostgreSQL')).toBe('#4169E1')
  })
})
