import type { ReactNode } from 'react'
import {
  siApachemaven, siClaude, siDocker, siFastapi, siFirebase, siFlutter, siFlyway, siGithubactions, siGooglecloud, 
  siHibernate, siJunit5, siLinux, siNextdotjs, siPostgresql, siPython, siReact, siRedis, siSpringboot, siSpringsecurity, siTypescript, siVuedotjs,
} from 'simple-icons'

interface Brand { path: string; hex: string }

/** Logos oficiais (simple-icons, licença CC0), embutidos no bundle: nenhuma imagem vem de outro servidor. */
const BRANDS: Record<string, Brand> = {
  'spring boot': siSpringboot, 'spring security': siSpringsecurity, 'hibernate / jpa': siHibernate, flyway: siFlyway,
  maven: siApachemaven, postgresql: siPostgresql, redis: siRedis, 'junit 5': siJunit5, junit: siJunit5, react: siReact, 'react native': siReact,
  typescript: siTypescript, 'next.js': siNextdotjs, flutter: siFlutter, docker: siDocker, 'github actions': siGithubactions,
  'google cloud': siGooglecloud, firebase: siFirebase, linux: siLinux, 'claude code': siClaude,
  python: siPython, fastapi: siFastapi, 'vue.js': siVuedotjs,
}

interface Glyph { color: string; draw: ReactNode }

const stroke = { fill: 'none', stroke: 'currentColor', strokeWidth: 2, strokeLinecap: 'round', strokeLinejoin: 'round' } as const

/** Símbolos próprios para o que não tem logo: conceitos e ferramentas sem marca no simple-icons. */
const GLYPHS: Record<string, Glyph> = {
  java: { color: '#e76f00', draw: <g {...stroke}><path d="M5 10h12v5a5 5 0 0 1-5 5H10a5 5 0 0 1-5-5z" /><path d="M17 11h1.5a2.5 2.5 0 0 1 0 5H17" /><path d="M9 3c-1 1.5 1 2.5 0 4M13 3c-1 1.5 1 2.5 0 4" /></g> },
  mockito: { color: '#78a641', draw: <g {...stroke}><path d="M6 4h12l-6 8z" /><path d="M12 12v7M8 20h8" /></g> },
  testcontainers: { color: '#0f7c8c', draw: <g {...stroke}><path d="M12 3 20 7.5v9L12 21l-8-4.5v-9z" /><path d="M4 7.5 12 12l8-4.5M12 12v9" /></g> },
  archunit: { color: '#7b4fb0', draw: <g {...stroke}><path d="M3 20h18M5 20V9M19 20V9M9.5 20V9M14.5 20V9M3 9l9-6 9 6z" /></g> },
  'aws (s3, sqs)': { color: '#ff9900', draw: <g {...stroke}><path d="M7 18h10a4 4 0 0 0 .5-7.97A6 6 0 0 0 6.1 9.6 4.2 4.2 0 0 0 7 18z" /></g> },
  hexagonal: { color: '#e4572e', draw: <g {...stroke}><path d="M12 2.5 20.5 7.3v9.4L12 21.5l-8.5-4.8V7.3z" /><path d="M12 8l3.5 2v4L12 16l-3.5-2v-4z" /></g> },
  'clean architecture': { color: '#2e86ab', draw: <g {...stroke}><circle cx="12" cy="12" r="9" /><circle cx="12" cy="12" r="5.5" /><circle cx="12" cy="12" r="2" /></g> },
  ddd: { color: '#3bb273', draw: <g {...stroke}><rect x="3" y="3" width="7.5" height="7.5" rx="1.5" /><rect x="13.5" y="3" width="7.5" height="7.5" rx="1.5" /><rect x="8.25" y="13.5" width="7.5" height="7.5" rx="1.5" /></g> },
  solid: { color: '#f2a541', draw: <g {...stroke}><path d="M4 20V8M8 20V5M12 20V10M16 20V6M20 20V12" /></g> },
  adrs: { color: '#5b6b7f', draw: <g {...stroke}><path d="M6 3h8l4 4v14H6z" /><path d="M14 3v4h4M9 12h6M9 16h4" /></g> },
  'spec-driven development': { color: '#22303f', draw: <g {...stroke}><path d="M9 6h11M9 12h11M9 18h11" /><path d="m3.5 6 1.5 1.5L7.5 5M3.5 12 5 13.5 7.5 11M3.5 18 5 19.5 7.5 17" /></g> },
  'engenharia de prompt': { color: '#d97757', draw: <g {...stroke}><path d="M4 5h16v11H9l-5 4z" /><path d="m8 9 2 1.5L8 12M12 12h4" /></g> },
  'integração com llms': { color: '#8e75b2', draw: <g {...stroke}><path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2.5 2.5M15.5 15.5 18 18M6 18l2.5-2.5M15.5 8.5 18 6" /></g> },
}
GLYPHS['prompt engineering'] = GLYPHS['engenharia de prompt']
GLYPHS['llm integration'] = GLYPHS['integração com llms']


/** Normaliza nomes de tags de projeto: "Java 21" e "Spring Boot 3" usam o ícone de "Java" e "Spring Boot". */
export function normalizeSkill(name: string): string {
  const aliases: Record<string, string> = { 'spring boot 4': 'spring boot', 'aws sqs': 'aws (s3, sqs)', 'spring security': 'spring security' }
  const key = name.toLowerCase().replace(/\s+\d+(\.\d+)?$/, '').trim()
  return aliases[name.toLowerCase()] ?? key
}

export function hasSkillIcon(name: string): boolean {
  const key = normalizeSkill(name)
  return key in BRANDS || key in GLYPHS
}

/** Cor de destaque de uma habilidade: a cor da marca, ou a do símbolo próprio. */
export function skillColor(name: string): string {
  const key = normalizeSkill(name)
  if (BRANDS[key]) return `#${BRANDS[key].hex}`
  return GLYPHS[key]?.color ?? '#7a7366'
}

export function SkillIcon({ name }: { name: string }) {
  const key = normalizeSkill(name)
  const brand = BRANDS[key]
  if (brand) {
    return (
      <svg className="skill-icon" viewBox="0 0 24 24" aria-hidden="true" style={{ color: `#${brand.hex}` }}>
        <path d={brand.path} fill="currentColor" />
      </svg>
    )
  }
  const glyph = GLYPHS[key]
  return (
    <svg className="skill-icon" viewBox="0 0 24 24" aria-hidden="true" style={{ color: glyph?.color ?? '#7a7366' }}>
      {glyph?.draw ?? <path d="M12 3 20 7.5v9L12 21l-8-4.5v-9z" fill="currentColor" opacity=".35" />}
    </svg>
  )
}
