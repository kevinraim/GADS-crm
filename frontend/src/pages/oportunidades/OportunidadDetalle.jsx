import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { obtenerOportunidad, cambiarEtapaOportunidad } from '../../api/oportunidades'
import { listarEtapas, listarMotivosPerdida } from '../../api/catalogo'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Etiqueta, { tonoDeEstadoOportunidad } from '../../components/Etiqueta'
import HistorialComercial from '../../components/HistorialComercial'
import { formatMoneda, formatFecha } from '../../utils/formato'

export default function OportunidadDetalle() {
  const { id } = useParams()
  const { etiqueta, opcionesEnum } = useAuth()

  const [oportunidad, setOportunidad] = useState(null)
  const [etapas, setEtapas] = useState([])
  const [motivos, setMotivos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const [etapaId, setEtapaId] = useState('')
  const [motivoPerdidaId, setMotivoPerdidaId] = useState('')
  const [observacion, setObservacion] = useState('')
  const [cambiandoEtapa, setCambiandoEtapa] = useState(false)

  const cargar = useCallback(() => {
    setCargando(true)
    obtenerOportunidad(id).then(setOportunidad).catch(setError).finally(() => setCargando(false))
  }, [id])

  useEffect(() => {
    cargar()
    listarEtapas().then(setEtapas).catch(() => {})
    listarMotivosPerdida().then(setMotivos).catch(() => {})
  }, [cargar])

  const etapaDestino = etapas.find((e) => e.id === etapaId)
  const esCambioAPerdida = etapaDestino?.tipo === 'PERDIDA'

  async function confirmarCambioEtapa() {
    if (!etapaId) return
    setCambiandoEtapa(true)
    setError(null)
    try {
      await cambiarEtapaOportunidad(id, {
        etapaId,
        observacion: observacion || undefined,
        motivoPerdidaId: esCambioAPerdida ? motivoPerdidaId || undefined : undefined,
      })
      setEtapaId('')
      setMotivoPerdidaId('')
      setObservacion('')
      cargar()
    } catch (err) {
      setError(err)
    } finally {
      setCambiandoEtapa(false)
    }
  }

  if (cargando) return <Cargando />
  if (!oportunidad) return null

  return (
    <div className="max-w-3xl">
      <div className="mb-4 flex items-start justify-between">
        <div>
          <h1 className="text-lg font-semibold">{oportunidad.titulo}</h1>
          <p className="text-sm text-texto/60">{oportunidad.empresaNombre || oportunidad.contactoNombre || 'Sin comercio asociado'}</p>
        </div>
        <div className="flex items-center gap-2">
          <Etiqueta texto={etiqueta('estadoOportunidad', oportunidad.estado)} tono={tonoDeEstadoOportunidad(oportunidad.estado)} />
          <Link to={`/oportunidades/${id}/editar`} className="rounded border border-linea px-3 py-1.5 text-sm">
            Editar
          </Link>
        </div>
      </div>

      <MensajeError error={error} />

      <div className="mb-6 grid grid-cols-2 gap-4 rounded border border-linea bg-superficie p-5 text-sm md:grid-cols-3">
        <Dato titulo="Etapa actual">{oportunidad.etapaActualNombre}</Dato>
        <Dato titulo="Responsable comercial">{oportunidad.responsableComercialNombre}</Dato>
        <Dato titulo="Origen">{oportunidad.origenNombre || '-'}</Dato>
        <Dato titulo="Valor estimado">{formatMoneda(oportunidad.valorEstimado)}</Dato>
        <Dato titulo="Fecha estimada de cierre">{formatFecha(oportunidad.fechaEstimadaCierre)}</Dato>
        <Dato titulo="Fecha real de cierre">{formatFecha(oportunidad.fechaRealCierre)}</Dato>
        <Dato titulo="Condición de pago negociada">{etiqueta('condicionPago', oportunidad.condicionPagoNegociada) || '-'}</Dato>
        <Dato titulo="Requiere alta de cta. cte.">{oportunidad.requiereAltaCuentaCorriente ? 'Sí' : 'No'}</Dato>
        {oportunidad.motivoPerdidaNombre && <Dato titulo="Motivo de pérdida">{oportunidad.motivoPerdidaNombre}</Dato>}
      </div>

      {oportunidad.items.length > 0 && (
        <section className="mb-6">
          <h2 className="mb-2 text-sm font-semibold text-texto/70">Lista de materiales</h2>
          <div className="overflow-hidden rounded border border-linea bg-superficie">
            <table className="w-full text-sm">
              <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
                <tr>
                  <th className="px-4 py-2 font-medium">Producto</th>
                  <th className="px-4 py-2 text-right font-medium">Cantidad</th>
                  <th className="px-4 py-2 text-right font-medium">Precio unit.</th>
                  <th className="px-4 py-2 text-right font-medium">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                {oportunidad.items.map((it, idx) => (
                  <tr key={idx} className="border-b border-linea last:border-0">
                    <td className="px-4 py-2">
                      {it.productoNombre} ({etiqueta('unidadVenta', it.unidadVenta)})
                    </td>
                    <td className="numero px-4 py-2 text-right">{it.cantidad}</td>
                    <td className="numero px-4 py-2 text-right">{formatMoneda(it.precioUnitario)}</td>
                    <td className="numero px-4 py-2 text-right">{formatMoneda(it.subtotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}

      {oportunidad.observaciones && (
        <div className="mb-6 rounded border border-linea bg-superficie p-4 text-sm">
          <p className="mb-1 text-xs uppercase text-texto/40">Observaciones</p>
          <p>{oportunidad.observaciones}</p>
        </div>
      )}

      <section>
        <h2 className="mb-2 text-sm font-semibold text-texto/70">Cambiar de etapa</h2>
        <div className="space-y-3 rounded border border-linea bg-superficie p-4">
          <div className="flex flex-wrap gap-2">
            <select value={etapaId} onChange={(e) => setEtapaId(e.target.value)} className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm">
              <option value="">Elegir etapa destino...</option>
              {etapas
                .filter((e) => e.id !== oportunidad.etapaActualId)
                .map((e) => (
                  <option key={e.id} value={e.id}>
                    {e.nombre}
                  </option>
                ))}
            </select>

            {esCambioAPerdida && (
              <select
                value={motivoPerdidaId}
                onChange={(e) => setMotivoPerdidaId(e.target.value)}
                className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
                required
              >
                <option value="">Elegí un motivo de pérdida...</option>
                {motivos.map((m) => (
                  <option key={m.valor} value={m.valor}>
                    {m.etiqueta}
                  </option>
                ))}
              </select>
            )}
          </div>

          <input
            type="text"
            placeholder="Observación (opcional)"
            value={observacion}
            onChange={(e) => setObservacion(e.target.value)}
            className="w-full rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
          />

          <button
            type="button"
            disabled={!etapaId || (esCambioAPerdida && !motivoPerdidaId) || cambiandoEtapa}
            onClick={confirmarCambioEtapa}
            className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
          >
            {cambiandoEtapa ? 'Guardando...' : 'Cambiar etapa'}
          </button>
        </div>
      </section>

      <div className="mt-6">
        <HistorialComercial oportunidadId={id} />
      </div>
    </div>
  )
}

function Dato({ titulo, children }) {
  return (
    <div>
      <p className="text-xs uppercase text-texto/40">{titulo}</p>
      <p className="mt-0.5">{children}</p>
    </div>
  )
}
