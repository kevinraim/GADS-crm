import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { obtenerMetricas } from '../api/metricas'
import { listarDistribuidoras } from '../api/distribuidoras'
import { useAuth } from '../auth/AuthContext'
import Cargando from '../components/Cargando'
import MensajeError from '../components/MensajeError'
import Vacio from '../components/Vacio'
import { formatMoneda, formatFechaHora } from '../utils/formato'

function Tarjeta({ titulo, valor, subtitulo }) {
  return (
    <div className="rounded border border-linea bg-superficie p-4">
      <p className="text-xs uppercase text-texto/50">{titulo}</p>
      <p className="numero mt-1 text-2xl font-semibold">{valor}</p>
      {subtitulo && <p className="mt-1 text-xs text-texto/50">{subtitulo}</p>}
    </div>
  )
}

export default function Metricas() {
  const { tieneRol } = useAuth()
  const esAdmin = tieneRol('ADMIN')

  const [metricas, setMetricas] = useState(null)
  const [distribuidoras, setDistribuidoras] = useState([])
  const [distribuidoraId, setDistribuidoraId] = useState('')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    obtenerMetricas({ distribuidoraId: distribuidoraId || undefined })
      .then(setMetricas)
      .catch(setError)
      .finally(() => setCargando(false))
  }, [distribuidoraId])

  useEffect(() => {
    if (esAdmin) {
      listarDistribuidoras().then(setDistribuidoras).catch(() => {})
    }
  }, [esAdmin])

  useEffect(() => {
    cargar()
  }, [cargar])

  if (cargando && !metricas) return <Cargando texto="Calculando métricas..." />

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
        <h1 className="text-lg font-semibold">Métricas</h1>
        {esAdmin && (
          <select
            value={distribuidoraId}
            onChange={(e) => setDistribuidoraId(e.target.value)}
            className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
          >
            <option value="">Agregado de todas las distribuidoras</option>
            {distribuidoras.map((d) => (
              <option key={d.id} value={d.id}>
                {d.nombreVisible}
              </option>
            ))}
          </select>
        )}
      </div>

      <MensajeError error={error} />

      {metricas && (
        <div className="space-y-6">
          <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
            <Tarjeta
              titulo="Tasa de conversión"
              valor={`${Math.round(metricas.tasaConversion.tasaGlobal * 100)}%`}
              subtitulo={`${metricas.tasaConversion.ganadas} ganadas / ${metricas.tasaConversion.perdidas} perdidas`}
            />
            <Tarjeta titulo="Pipeline abierto" valor={formatMoneda(metricas.pipelineAbierto)} />
            <Tarjeta
              titulo="Comercios sin actividad reciente"
              valor={metricas.comerciosSinActividadReciente.length}
              subtitulo="Últimos 30 días"
            />
            <Tarjeta
              titulo="Comercios nuevos (últ. mes)"
              valor={metricas.comerciosNuevosPorMes.at(-1)?.cantidad ?? 0}
            />
          </div>

          <section>
            <h2 className="mb-2 text-sm font-semibold text-texto/70">Oportunidades por etapa</h2>
            <div className="space-y-1.5 rounded border border-linea bg-superficie p-4">
              {metricas.oportunidadesPorEtapa.map((e, idx) => {
                const max = Math.max(...metricas.oportunidadesPorEtapa.map((x) => x.cantidad), 1)
                return (
                  <div key={e.etapaId || idx} className="flex items-center gap-3 text-sm">
                    <span className="w-40 shrink-0 truncate text-texto/70">{e.etapaNombre}</span>
                    <div className="h-4 flex-1 rounded bg-fondo">
                      <div
                        className="h-4 rounded bg-acento/70"
                        style={{ width: `${(e.cantidad / max) * 100}%` }}
                      />
                    </div>
                    <span className="numero w-12 shrink-0 text-right">{e.cantidad}</span>
                    <span className="numero w-28 shrink-0 text-right text-texto/60">{formatMoneda(e.valorTotal)}</span>
                  </div>
                )
              })}
            </div>
          </section>

          <div className="grid gap-6 md:grid-cols-2">
            <section>
              <h2 className="mb-2 text-sm font-semibold text-texto/70">Ranking de responsables comerciales</h2>
              {metricas.rankingResponsables.length === 0 ? (
                <Vacio titulo="Todavía no hay oportunidades ganadas." />
              ) : (
                <div className="divide-y divide-linea rounded border border-linea bg-superficie">
                  {metricas.rankingResponsables.map((r) => (
                    <div key={r.responsableId} className="flex items-center justify-between px-4 py-2.5 text-sm">
                      <span className="font-medium">{r.responsableNombre || 'Sin nombre'}</span>
                      <span className="text-texto/60">{r.cantidadGanadas} ganadas</span>
                      <span className="numero">{formatMoneda(r.valorGanado)}</span>
                    </div>
                  ))}
                </div>
              )}
            </section>

            <section>
              <h2 className="mb-2 text-sm font-semibold text-texto/70">Motivos de pérdida más frecuentes</h2>
              {metricas.motivosPerdidaFrecuentes.length === 0 ? (
                <Vacio titulo="Todavía no hay oportunidades perdidas." />
              ) : (
                <div className="divide-y divide-linea rounded border border-linea bg-superficie">
                  {metricas.motivosPerdidaFrecuentes.map((m) => (
                    <div key={m.motivoId} className="flex items-center justify-between px-4 py-2.5 text-sm">
                      <span>{m.motivoNombre || 'Sin motivo'}</span>
                      <span className="numero text-texto/60">{m.cantidad}</span>
                    </div>
                  ))}
                </div>
              )}
            </section>
          </div>

          <section>
            <h2 className="mb-2 text-sm font-semibold text-texto/70">Comercios sin actividad reciente</h2>
            {metricas.comerciosSinActividadReciente.length === 0 ? (
              <Vacio titulo="Todos los comercios tienen actividad o pedidos recientes." />
            ) : (
              <div className="divide-y divide-linea rounded border border-linea bg-superficie">
                {metricas.comerciosSinActividadReciente.map((c) => (
                  <Link
                    key={c.empresaId}
                    to={`/comercios/${c.empresaId}`}
                    className="flex items-center justify-between px-4 py-2.5 text-sm hover:bg-fondo/40"
                  >
                    <span className="font-medium text-acento">{c.empresaNombre}</span>
                    <span className="text-texto/60">
                      {c.ultimaActividad ? `Última actividad: ${formatFechaHora(c.ultimaActividad)}` : 'Sin actividad registrada'}
                    </span>
                  </Link>
                ))}
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  )
}
