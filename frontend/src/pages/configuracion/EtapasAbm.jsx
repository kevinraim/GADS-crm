import { useCallback, useEffect, useState } from 'react'
import { listarCatalogoAdministracion, crearItemCatalogo, actualizarItemCatalogo, cambiarEstadoItemCatalogo } from '../../api/catalogo'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Etiqueta from '../../components/Etiqueta'
import Campo, { claseInput } from '../../components/Campo'

const VACIO = { nombre: '', descripcion: '', orden: 0, tipo: 'ABIERTA', color: '#0E5C8A' }

export default function EtapasAbm() {
  const { opcionesEnum } = useAuth()
  const [etapas, setEtapas] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [form, setForm] = useState(VACIO)
  const [editandoId, setEditandoId] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const cargar = useCallback(() => {
    setCargando(true)
    listarCatalogoAdministracion('etapas').then(setEtapas).catch(setError).finally(() => setCargando(false))
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: campo === 'orden' ? Number(evento.target.value) : evento.target.value }))
  }

  function editar(etapa) {
    setEditandoId(etapa.id)
    setForm({ nombre: etapa.nombre, descripcion: etapa.descripcion || '', orden: etapa.orden, tipo: etapa.tipo, color: etapa.color || '#0E5C8A' })
  }

  function cancelarEdicion() {
    setEditandoId(null)
    setForm(VACIO)
  }

  async function guardar(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      if (editandoId) {
        await actualizarItemCatalogo('etapas', editandoId, form)
      } else {
        await crearItemCatalogo('etapas', form)
      }
      cancelarEdicion()
      cargar()
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  async function alternarEstado(etapa) {
    try {
      await cambiarEstadoItemCatalogo('etapas', etapa.id, !etapa.activa)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  return (
    <div>
      <p className="mb-4 text-sm text-texto/60">
        Las etapas definen las columnas del embudo. El tipo determina si cuentan como abierta, ganada o perdida.
      </p>

      <MensajeError error={error} />

      <form onSubmit={guardar} className="mb-4 space-y-3 rounded border border-linea bg-superficie p-4">
        <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
          <Campo label="Nombre" required>
            <input className={claseInput} value={form.nombre} onChange={cambiar('nombre')} required />
          </Campo>
          <Campo label="Orden">
            <input type="number" className={claseInput} value={form.orden} onChange={cambiar('orden')} />
          </Campo>
          <Campo label="Tipo" required>
            <select className={claseInput} value={form.tipo} onChange={cambiar('tipo')}>
              {opcionesEnum('tipoEtapa').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Color">
            <input type="color" className={`${claseInput} h-9 p-1`} value={form.color} onChange={cambiar('color')} />
          </Campo>
        </div>
        <Campo label="Descripción">
          <input className={claseInput} value={form.descripcion} onChange={cambiar('descripcion')} />
        </Campo>
        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {editandoId ? 'Guardar cambios' : 'Agregar etapa'}
          </button>
          {editandoId && (
            <button type="button" onClick={cancelarEdicion} className="rounded border border-linea px-4 py-2 text-sm">
              Cancelar
            </button>
          )}
        </div>
      </form>

      {cargando && <Cargando />}

      {!cargando && etapas && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Etapa</th>
                <th className="px-4 py-2.5 font-medium">Tipo</th>
                <th className="px-4 py-2.5 font-medium">Orden</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
                <th className="px-4 py-2.5 font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {etapas.map((etapa) => (
                <tr key={etapa.id} className="border-b border-linea last:border-0">
                  <td className="px-4 py-2.5">
                    <span className="mr-2 inline-block h-2.5 w-2.5 rounded-full align-middle" style={{ backgroundColor: etapa.color }} />
                    <span className="font-medium">{etapa.nombre}</span>
                  </td>
                  <td className="px-4 py-2.5">{etapa.tipo}</td>
                  <td className="px-4 py-2.5">{etapa.orden}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={etapa.activa ? 'Activa' : 'Inactiva'} tono={etapa.activa ? 'cliente' : 'inactivo'} />
                  </td>
                  <td className="px-4 py-2.5 space-x-3">
                    <button type="button" onClick={() => editar(etapa)} className="text-sm text-acento hover:underline">
                      Editar
                    </button>
                    <button type="button" onClick={() => alternarEstado(etapa)} className="text-sm text-acento hover:underline">
                      {etapa.activa ? 'Desactivar' : 'Activar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
