import type { Lang, Step } from './types'

export type SectionId = 'about' | 'experience' | 'projects' | 'skills' | 'education' | 'contact'

/** Ordem dos lados do hexágono, no sentido horário a partir do lado de cima à esquerda. */
export const SECTIONS: SectionId[] = ['about', 'experience', 'projects', 'skills', 'education', 'contact']

const pt = {
  sections: { about: 'Sobre', experience: 'Experiência', projects: 'Projetos', skills: 'Habilidades', education: 'Formação', contact: 'Contato' },
  location: 'Local', status: 'Status',
  tagline: 'Projeto backends Java com arquitetura hexagonal e testes automatizados. Este site é um deles.',
  devCta: 'Ligue o modo dev para ver o backend por trás desta página.',
  liveTest: 'Teste ao vivo',
  liveTestHint: 'Envie uma mensagem e veja ela atravessar o hexágono: HTTP, caso de uso, fila SQS, consumidor e e-mail.',
  piece: 'Peça selecionada',
  devMode: 'modo dev',
  devHintOff: 'Ligue o modo dev para ver a arquitetura real que serve esta página.',
  devHintOn: 'Este hexágono é o backend que serve a página: cada peça vem de GET /api/architecture e leva ao código.',
  privateCode: 'código privado da empresa',
  moreHighlights: 'ver destaques', lessHighlights: 'esconder destaques',
  repo: 'código',
  demo: 'demo',
  name: 'Nome', email: 'E-mail', message: 'Mensagem',
  send: 'Enviar mensagem', sending: 'Enviando…',
  sent: 'Mensagem recebida. Respondo em breve!',
  sendAnother: 'Enviar outra',
  offline: 'O servidor não respondeu. Você pode me escrever direto:',
  tooMany: 'Muitas mensagens em pouco tempo. Tente de novo mais tarde.',
  watchLive: 'Com o modo dev ligado, você vê a mensagem percorrer o hexágono ao vivo.',
  wakingUp: 'acordando o servidor…',
  apiNotYet: 'a API ainda não foi publicada',
  contactSoon: 'O formulário de contato entra no ar junto com a API. Enquanto isso, fale comigo pelo LinkedIn:',
  fromApi: 'da API', fromSnapshot: 'da cópia estática',
  showResponse: 'ver resposta', hideResponse: 'esconder',
  servedBy: 'servido por',
  openSource: 'abrir no GitHub',
  layers: { inbound: 'adaptadores de entrada', outbound: 'adaptadores de saída', core: 'aplicação', domain: 'domínio' },
  kinds: {
    DOMAIN_MODEL: 'modelo de domínio', USE_CASE: 'caso de uso', INBOUND_PORT: 'porta de entrada',
    OUTBOUND_PORT: 'porta de saída', INBOUND_ADAPTER: 'adaptador de entrada', OUTBOUND_ADAPTER: 'adaptador de saída',
  },
  steps: {
    RECEIVED: 'recebida', ENQUEUED: 'na fila (SQS)', PROCESSING: 'processando', ATTEMPT_FAILED: 'tentativa falhou',
    NOTIFIED: 'e-mail enviado', FAILED: 'foi para a DLQ',
  } satisfies Record<Step, string>,
  selectHint: 'Clique numa peça para ver o que ela faz.',
  scrollMore: 'Rolar para ver mais',
  theme: 'tema',
}

const en: typeof pt = {
  sections: { about: 'About', experience: 'Experience', projects: 'Projects', skills: 'Skills', education: 'Education', contact: 'Contact' },
  location: 'Location', status: 'Status',
  tagline: 'I design Java back-ends with hexagonal architecture and automated tests. This site is one of them.',
  devCta: 'Turn on dev mode to see the back-end behind this page.',
  liveTest: 'Live test',
  liveTestHint: 'Send a message and watch it cross the hexagon: HTTP, use case, SQS queue, consumer and email.',
  piece: 'Selected piece',
  devMode: 'dev mode',
  devHintOff: 'Turn on dev mode to see the real architecture serving this page.',
  devHintOn: 'This hexagon is the back-end serving the page: every piece comes from GET /api/architecture and links to the code.',
  privateCode: 'company code (private)',
  moreHighlights: 'show highlights', lessHighlights: 'hide highlights',
  repo: 'code',
  demo: 'demo',
  name: 'Name', email: 'Email', message: 'Message',
  send: 'Send message', sending: 'Sending…',
  sent: 'Message received. I will reply soon!',
  sendAnother: 'Send another',
  offline: 'The server did not answer. You can email me directly:',
  tooMany: 'Too many messages in a short time. Please try again later.',
  watchLive: 'With dev mode on, you can watch the message travel through the hexagon live.',
  wakingUp: 'waking up the server…',
  apiNotYet: 'the API is not published yet',
  contactSoon: 'The contact form goes live together with the API. Meanwhile, reach me on LinkedIn:',
  fromApi: 'from the API', fromSnapshot: 'from the static snapshot',
  showResponse: 'show response', hideResponse: 'hide',
  servedBy: 'served by',
  openSource: 'open on GitHub',
  layers: { inbound: 'inbound adapters', outbound: 'outbound adapters', core: 'application', domain: 'domain' },
  kinds: {
    DOMAIN_MODEL: 'domain model', USE_CASE: 'use case', INBOUND_PORT: 'inbound port',
    OUTBOUND_PORT: 'outbound port', INBOUND_ADAPTER: 'inbound adapter', OUTBOUND_ADAPTER: 'outbound adapter',
  },
  steps: {
    RECEIVED: 'received', ENQUEUED: 'queued (SQS)', PROCESSING: 'processing', ATTEMPT_FAILED: 'attempt failed',
    NOTIFIED: 'email sent', FAILED: 'sent to the DLQ',
  },
  selectHint: 'Click a piece to see what it does.',
  scrollMore: 'Scroll for more',
  theme: 'theme',
}

export const STRINGS: Record<Lang, typeof pt> = { pt, en }
export type Strings = typeof pt

/** Qual adaptador de entrada serve cada seção: o rodapé do modo dev aponta para ele. */
export const SECTION_SOURCE: Record<SectionId, string> = {
  about: 'PortfolioController', experience: 'PortfolioController', projects: 'PortfolioController',
  skills: 'PortfolioController', education: 'PortfolioController', contact: 'ContactController',
}
