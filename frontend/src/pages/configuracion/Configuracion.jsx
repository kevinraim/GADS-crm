import { useState } from 'react'
import EtapasAbm from './EtapasAbm'
import CatalogoSimpleAbm from './CatalogoSimpleAbm'

const PESTANIAS = [
  { id: 'etapas', texto: 'Etapas' },
  { id: 'origenes', texto: 'Orígenes' },
  { id: 'motivos', texto: 'Motivos de pérdida' },
  { id: 'tiposActividad', texto: 'Tipos de actividad' },
]

export default function Configuracion() {
  const [pestania, setPestania] = useState('etapas')

  return (
    <div>
      <h1 className="mb-1 text-lg font-semibold">Configuración</h1>
      <p className="mb-4 text-sm text-texto/60">
        Catálogos propios de tu distribuidora: etapas del embudo, orígenes, motivos de pérdida y tipos de actividad.
      </p>

      <div className="mb-4 flex gap-1 border-b border-linea">
        {PESTANIAS.map((p) => (
          <button
            key={p.id}
            type="button"
            onClick={() => setPestania(p.id)}
            className={`border-b-2 px-3 py-2 text-sm ${
              pestania === p.id ? 'border-acento font-medium text-acento' : 'border-transparent text-texto/60 hover:text-texto'
            }`}
          >
            {p.texto}
          </button>
        ))}
      </div>

      {pestania === 'etapas' && <EtapasAbm />}
      {pestania === 'origenes' && <CatalogoSimpleAbm recurso="origenes" titulo="Origen" />}
      {pestania === 'motivos' && <CatalogoSimpleAbm recurso="motivos" titulo="Motivo" />}
      {pestania === 'tiposActividad' && <CatalogoSimpleAbm recurso="tiposActividad" titulo="Tipo de actividad" />}
    </div>
  )
}
