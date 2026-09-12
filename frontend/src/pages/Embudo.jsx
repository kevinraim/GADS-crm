import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { obtenerEmbudo } from '../api/embudo'
import { cambiarEtapaOportunidad } from '../api/oportunidades'
import { listarUsuarios } from '../api/usuarios'
import { formatMoneda } from '../utils/formato'
import Cargando from '../components/Cargando'
import MensajeError from '../components/MensajeError'
import Vacio from '../components/Vacio'

export default function Embudo() {
  const [embudo, setEmbudo] = useState(null)
  const [usuarios, setUsuarios] = useState([])
  const [responsableId, setResponsableId] = useState('')
  const [zona, setZona] = useState('')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [arrastrandoId, setArrastrandoId] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    obtenerEmbudo({ responsableId: responsableId || undefined, zona: zona || undefined })
      .then(setEmbudo)
      .catch(setError)
      .finally(() => setCargando(false))
  }, [responsableId, zona])

  useEffect(() => {
    listarUsuarios().then(setUsuarios).catch(() => {})
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  async function soltarEnColumna(evento, etapaId) {
    evento.preventDefault()
    const oportunidadId = evento.dataTransfer.getData('text/plain')
    setArrastrandoId(null)
    if (!oportunidadId) return

    // Optimista: si falla, se revierte recargando el embudo y se muestra el error del backend.
    const embudoAnterior = embudo
    try {
      await cambiarEtapaOportunidad(oportunidadId, { etapaId })
      cargar()
    } catch (err) {
      setEmbudo(embudoAnterior)
      setError(err)
    }
  }

  if (cargando && !embudo) return <Cargando texto="Cargando el embudo..." />

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-lg font-semibold">Embudo comercial</h1>
          <p className="text-sm text-texto/60">Arrastrá una tarjeta a otra columna para cambiarla de etapa.</p>
        </div>

        <div className="flex gap-2">
          <select
            value={responsableId}
            onChange={(e) => setResponsableId(e.target.value)}
            className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
          >
            <option value="">Todos los responsables</option>
            {usuarios.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nombreCompleto}
              </option>
            ))}
          </select>
          <input
            type="text"
            placeholder="Filtrar por zona..."
            value={zona}
            onChange={(e) => setZona(e.target.value)}
            className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
          />
        </div>
      </div>

      <MensajeError error={error} />

      <div className="flex gap-4 overflow-x-auto pb-4">
        {embudo?.columnas.map((columna) => (
          <div
            key={columna.etapaId}
            onDragOver={(e) => e.preventDefault()}
            onDrop={(e) => soltarEnColumna(e, columna.etapaId)}
            className="flex w-72 shrink-0 flex-col rounded border border-linea bg-superficie"
          >
            <div className="border-t-4 px-3 py-2.5" style={{ borderTopColor: columna.color }}>
              <p className="text-sm font-semibold">{columna.etapaNombre}</p>
              <p className="text-xs text-texto/60">
                {columna.cantidad} oportunidad{columna.cantidad === 1 ? '' : 'es'} · {formatMoneda(columna.valorTotal)}
              </p>
            </div>

            <div className="flex-1 space-y-2 p-2">
              {columna.oportunidades.length === 0 && (
                <p className="px-2 py-6 text-center text-xs text-texto/40">Sin oportunidades en esta etapa</p>
              )}
              {columna.oportunidades.map((oportunidad) => (
                <Link
                  key={oportunidad.id}
                  to={`/oportunidades/${oportunidad.id}`}
                  draggable
                  onDragStart={(e) => {
                    e.dataTransfer.setData('text/plain', oportunidad.id)
                    setArrastrandoId(oportunidad.id)
                  }}
                  onDragEnd={() => setArrastrandoId(null)}
                  className={`block rounded border border-linea bg-fondo/40 p-2.5 text-sm hover:border-acento ${
                    arrastrandoId === oportunidad.id ? 'opacity-40' : ''
                  }`}
                >
                  <p className="font-medium leading-snug">{oportunidad.titulo}</p>
                  <p className="mt-0.5 text-xs text-texto/60">{oportunidad.clienteNombre || 'Sin comercio asociado'}</p>
                  <div className="mt-1.5 flex items-center justify-between">
                    <span className="numero text-xs font-medium">{formatMoneda(oportunidad.valorEstimado)}</span>
                    {oportunidad.requiereAltaCuentaCorriente && (
                      <span className="rounded bg-acento/10 px-1.5 py-0.5 text-[10px] text-acento">Cta. cte.</span>
                    )}
                  </div>
                </Link>
              ))}
            </div>
          </div>
        ))}

        {!embudo?.columnas?.length && (
          <Vacio titulo="No hay etapas configuradas" subtitulo="Contactá al administrador del sistema." />
        )}
      </div>
    </div>
  )
}
