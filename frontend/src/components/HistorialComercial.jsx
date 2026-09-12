import { useCallback, useEffect, useState } from 'react'
import { listarActividades, crearActividad } from '../api/actividades'
import { listarTiposActividad } from '../api/catalogo'
import { historialEtapasOportunidad } from '../api/oportunidades'
import Cargando from './Cargando'
import MensajeError from './MensajeError'
import Vacio from './Vacio'
import Campo, { claseInput } from './Campo'
import { formatFechaHora } from '../utils/formato'

const VACIO = { tipoActividadId: '', descripcion: '' }

/**
 * Historial comercial combinado: actividades (llamadas, visitas, etc.) y, solo para oportunidades,
 * cambios de etapa. Se usa en el detalle de Comercio, Contacto y Oportunidad.
 */
export default function HistorialComercial({ empresaId, contactoId, oportunidadId }) {
  const [eventos, setEventos] = useState(null)
  const [tiposActividad, setTiposActividad] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [form, setForm] = useState(VACIO)
  const [enviando, setEnviando] = useState(false)

  const cargar = useCallback(() => {
    setCargando(true)
    const pedidos = [listarActividades({ empresaId, contactoId, oportunidadId })]
    pedidos.push(oportunidadId ? historialEtapasOportunidad(oportunidadId) : Promise.resolve([]))

    Promise.all(pedidos)
      .then(([actividades, cambiosEtapa]) => {
        const eventosActividad = actividades.map((a) => ({
          tipo: 'actividad',
          fecha: a.fecha,
          titulo: a.tipoActividadNombre || 'Actividad',
          descripcion: a.descripcion,
          usuario: a.usuarioNombre,
        }))
        const eventosEtapa = cambiosEtapa.map((h) => ({
          tipo: 'etapa',
          fecha: h.fechaHora,
          titulo: h.etapaAnteriorNombre ? `${h.etapaAnteriorNombre} → ${h.etapaNuevaNombre}` : `Etapa inicial: ${h.etapaNuevaNombre}`,
          descripcion: h.observacion,
          usuario: h.usuarioEmail,
        }))
        setEventos([...eventosActividad, ...eventosEtapa].sort((a, b) => new Date(b.fecha) - new Date(a.fecha)))
      })
      .catch(setError)
      .finally(() => setCargando(false))
  }, [empresaId, contactoId, oportunidadId])

  useEffect(() => {
    listarTiposActividad().then(setTiposActividad).catch(() => {})
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: evento.target.value }))
  }

  async function agregarActividad(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      await crearActividad({ ...form, empresaId, contactoId, oportunidadId })
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section>
      <h2 className="mb-2 text-sm font-semibold text-texto/70">Historial comercial</h2>

      <MensajeError error={error} />

      <form onSubmit={agregarActividad} className="mb-3 flex flex-wrap items-end gap-2 rounded border border-linea bg-superficie p-3">
        <Campo label="Tipo de actividad" required>
          <select className={`${claseInput} w-48`} value={form.tipoActividadId} onChange={cambiar('tipoActividadId')} required>
            <option value="">Elegir...</option>
            {tiposActividad.map((t) => (
              <option key={t.valor} value={t.valor}>
                {t.etiqueta}
              </option>
            ))}
          </select>
        </Campo>
        <Campo label="Descripción" required>
          <input className={`${claseInput} min-w-[260px]`} value={form.descripcion} onChange={cambiar('descripcion')} required />
        </Campo>
        <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
          {enviando ? 'Guardando...' : 'Registrar'}
        </button>
      </form>

      {cargando && <Cargando />}

      {!cargando && eventos && eventos.length === 0 && <Vacio titulo="Todavía no hay actividades registradas." />}

      {!cargando && eventos && eventos.length > 0 && (
        <ol className="space-y-2">
          {eventos.map((evento, idx) => (
            <li key={idx} className="rounded border border-linea bg-superficie p-3 text-sm">
              <div className="flex items-center justify-between">
                <span className="font-medium">{evento.titulo}</span>
                <span className="text-xs text-texto/50">{formatFechaHora(evento.fecha)}</span>
              </div>
              {evento.descripcion && <p className="mt-1 text-texto/70">{evento.descripcion}</p>}
              {evento.usuario && <p className="mt-1 text-xs text-texto/40">{evento.usuario}</p>}
            </li>
          ))}
        </ol>
      )}
    </section>
  )
}
