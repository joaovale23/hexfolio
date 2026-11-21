export type Lang = 'pt' | 'en'

export interface Portfolio {
  lang: Lang
  profile: { name: string; role: string; location: string; status: string; bio: string; tags: string[] }
  experiences: { company: string; role: string; period: string; location: string; description: string; highlights: string[] }[]
  projects: {
    slug: string; name: string; description: string; highlights: string[]; tags: string[]
    status: string; repoUrl: string | null; demoUrl: string | null; privateCode: boolean
  }[]
  skillGroups: { title: string; skills: string[] }[]
  education: { course: string; institution: string; period: string }[]
}

export type Kind = 'DOMAIN_MODEL' | 'USE_CASE' | 'INBOUND_PORT' | 'OUTBOUND_PORT' | 'INBOUND_ADAPTER' | 'OUTBOUND_ADAPTER'

export interface ArchComponent { id: string; kind: Kind; description: string; className: string; sourceUrl: string }
export interface ArchLink { from: string; to: string; relation: 'IMPLEMENTS' | 'USES' }
export interface ArchitectureMap { components: ArchComponent[]; links: ArchLink[] }

export type Step = 'RECEIVED' | 'ENQUEUED' | 'PROCESSING' | 'ATTEMPT_FAILED' | 'NOTIFIED' | 'FAILED'
export interface TraceEvent { step: Step; at: string; tookMs: number; component: string }

/** Como um dado chegou à tela: da API ao vivo ou da cópia estática gerada no build. */
export interface Loaded<T> {
  data: T
  source: 'api' | 'snapshot'
  request: string
  status?: number
  ms?: number
}
