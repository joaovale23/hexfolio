import type { ArchitectureMap, Step } from '../types'

/**
 * Que peças acendem numa etapa do contato. O backend informa a porta ou o caso de uso;
 * aqui somamos quem o implementa e o adaptador de entrada que o aciona.
 */
export function componentsLitBy(map: ArchitectureMap, component: string): Set<string> {
  const lit = new Set([component])
  for (const link of map.links) {
    if (link.to === component && link.relation === 'IMPLEMENTS') lit.add(link.from)
  }
  const inboundAdapters = new Set(map.components.filter(c => c.kind === 'INBOUND_ADAPTER').map(c => c.id))
  for (const link of map.links) {
    if (link.to === component && link.relation === 'USES' && inboundAdapters.has(link.from)) lit.add(link.from)
  }
  return lit
}

export const isFailure = (step: Step) => step === 'ATTEMPT_FAILED' || step === 'FAILED'

/** Ligações entre duas peças acesas: são as linhas que brilham no caminho. */
export function linksBetween(map: ArchitectureMap, lit: Set<string>) {
  return map.links.filter(l => lit.has(l.from) && lit.has(l.to))
}
