import { useState } from 'react'
import { HAS_API } from '../api'
import type { Strings } from '../i18n'
import type { ArchComponent, Loaded } from '../types'

interface Props { loaded: Loaded<unknown>; slice: unknown; servedBy?: ArchComponent; t: Strings }


/** Rodapé do modo dev: a requisição que trouxe esta seção, o JSON e a classe que respondeu. */
export function ApiPeek({ loaded, slice, servedBy, t }: Props) {
  const [open, setOpen] = useState(false)
  return (
    <div className="peek">
      <div className="peek-line">
        <code>{loaded.request}</code>
        {loaded.source === 'api'
          ? <span className="peek-ok">{loaded.status} · {loaded.ms} ms · {t.fromApi}</span>
          : <span className="peek-warn">{t.fromSnapshot} · {HAS_API ? t.wakingUp : t.apiNotYet}</span>}
        {servedBy && (
          <span className="peek-by">{t.servedBy} <a href={servedBy.sourceUrl} target="_blank" rel="noreferrer">{servedBy.id}</a></span>
        )}
        <button type="button" className="link-button" onClick={() => setOpen(o => !o)}>
          {open ? t.hideResponse : t.showResponse}
        </button>
      </div>
      {open && <pre className="peek-json">{JSON.stringify(slice, null, 2)}</pre>}
    </div>
  )
}
