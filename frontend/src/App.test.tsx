import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import architecture from '../public/snapshot/architecture.json'
import portfolio from '../public/snapshot/portfolio.pt.json'
import App from './App'

beforeEach(() => {
  Object.defineProperty(navigator, 'language', { value: 'pt-BR', configurable: true })
  // A API "não responde"; a página tem que funcionar só com a cópia estática.
  vi.spyOn(globalThis, 'fetch').mockImplementation(async input => {
    const url = String(input)
    if (url.includes('snapshot/portfolio')) return Response.json(portfolio)
    if (url.includes('snapshot/architecture')) return Response.json(architecture)
    throw new TypeError('offline')
  })
})

describe('App', () => {
  it('mostra quem sou e os seis lados do hexágono, mesmo com a API fora do ar', async () => {
    render(<App />)
    expect(await screen.findByText('João Vitor Vale')).toBeInTheDocument()
    for (const label of ['Sobre', 'Experiência', 'Projetos', 'Habilidades', 'Formação', 'Contato']) {
      expect(screen.getByRole('button', { name: label })).toBeInTheDocument()
    }
    expect(screen.getByRole('heading', { name: 'Sobre' })).toBeInTheDocument()
  })

  it('troca de seção ao clicar num lado', async () => {
    render(<App />)
    await userEvent.click(await screen.findByRole('button', { name: 'Projetos' }))
    const panel = screen.getByRole('region', { name: 'Projetos' })
    expect(within(panel).getByText('ServiJá')).toBeInTheDocument()
    expect(within(panel).getAllByText('código privado da empresa').length).toBeGreaterThan(0)
  })

  it('no modo dev, desenha as peças reais da arquitetura', async () => {
    render(<App />)
    await userEvent.click(await screen.findByRole('switch'))
    expect(await screen.findByRole('button', { name: 'adaptador de entrada: SqsContactListener' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'porta de saída: ContactQueue' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Teste ao vivo' })).toBeInTheDocument()
  })

  it('mostra a requisição real ao clicar no controller que serviu a página', async () => {
    render(<App />)
    await userEvent.click(await screen.findByRole('switch'))
    await userEvent.click(await screen.findByRole('button', { name: 'adaptador de entrada: PortfolioController' }))
    expect(screen.getByText('GET /api/portfolio?lang=pt')).toBeInTheDocument()
    expect(screen.getByText(/da cópia estática/)).toBeInTheDocument()
  })

  it('mostra os detalhes de uma peça ao clicar nela', async () => {
    render(<App />)
    await userEvent.click(await screen.findByRole('switch'))
    await userEvent.click(await screen.findByRole('button', { name: 'caso de uso: ProcessContactService' }))
    expect(screen.getByRole('heading', { name: 'ProcessContactService' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /abrir no GitHub/ })).toHaveAttribute('href', expect.stringContaining('ProcessContactService.java'))
  })
})
