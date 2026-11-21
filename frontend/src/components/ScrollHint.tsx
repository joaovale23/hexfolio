import { useEffect, useState, type RefObject } from 'react'

interface Props {
  /** Contêiner com rolagem própria. Sem ele, olha a rolagem da página. */
  container?: RefObject<HTMLElement | null>
  label: string
}

/** Quanto falta rolar até o fim, em pixels. */
function remaining(el: HTMLElement | null): number {
  if (el) return el.scrollHeight - el.clientHeight - el.scrollTop
  const doc = document.documentElement
  return doc.scrollHeight - window.innerHeight - window.scrollY
}

/**
 * Setinha translúcida que "quer descer": aparece só quando há conteúdo abaixo e some ao chegar no fim.
 * Fica grudada no rodapé (position: sticky) da coluna onde é colocada.
 */
export function ScrollHint({ container, label }: Props) {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const el = container?.current ?? null
    const target: HTMLElement | Window = el ?? window
    const update = () => setVisible(remaining(el) > 48)
    update()
    target.addEventListener('scroll', update, { passive: true })
    window.addEventListener('resize', update)
    // O conteúdo muda de altura ao trocar de seção ou abrir destaques.
    const observer = typeof ResizeObserver === 'undefined' ? null : new ResizeObserver(update)
    observer?.observe(el ?? document.body)
    if (el) Array.from(el.children).forEach(child => observer?.observe(child))
    return () => {
      target.removeEventListener('scroll', update)
      window.removeEventListener('resize', update)
      observer?.disconnect()
    }
  }, [container])

  const scrollDown = () => {
    const el = container?.current
    const by = (el?.clientHeight ?? window.innerHeight) * 0.7
    ;(el ?? window).scrollBy({ top: by, behavior: 'smooth' })
  }

  return (
    <div className={`scroll-hint${visible ? ' is-visible' : ''}`} aria-hidden={!visible}>
      <button type="button" onClick={scrollDown} aria-label={label} tabIndex={visible ? 0 : -1}>
        <svg viewBox="0 0 40 14" aria-hidden="true"><path d="M2 2 20 12 38 2" /></svg>
      </button>
    </div>
  )
}
