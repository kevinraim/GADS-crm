import { useCallback, useEffect, useState } from 'react'
import {
  listarCatalogoAdministracion,
  crearItemCatalogo,
  actualizarItemCatalogo,
  cambiarEstadoItemCatalogo,
} from '../../api/catalogo'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Etiqueta from '../../components/Etiqueta'
import Campo, { claseInput } from '../../components/Campo'

const VACIO = { nombre: '', orden: 0 }

// ABM genérico para Origen, MotivoPerdida y TipoActividad: misma forma (nombre, orden, activo).
export default function CatalogoSimpleAbm({ recurso, titulo }) {
  const [items, setItems] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [form, setForm] = useState(VACIO)
  const [editandoId, setEditandoId] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const cargar = useCallback(() => {
    setCargando(true)
    listarCatalogoAdministracion(recurso).then(setItems).catch(setError).finally(() => setCargando(false))
  }, [recurso])

  useEffect(() => {
    cargar()
  }, [cargar])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: campo === 'orden' ? Number(evento.target.value) : evento.target.value }))
  }

  function editar(item) {
    setEditandoId(item.id)
    setForm({ nombre: item.nombre, orden: item.orden })
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
        await actualizarItemCatalogo(recurso, editandoId, form)
      } else {
        await crearItemCatalogo(recurso, form)
      }
      cancelarEdicion()
      cargar()
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  async function alternarEstado(item) {
    try {
      await cambiarEstadoItemCatalogo(recurso, item.id, !item.activo)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  return (
    <div>
      <MensajeError error={error} />

      <form onSubmit={guardar} className="mb-4 flex flex-wrap items-end gap-2 rounded border border-linea bg-superficie p-4">
        <Campo label="Nombre" required>
          <input className={claseInput} value={form.nombre} onChange={cambiar('nombre')} required />
        </Campo>
        <Campo label="Orden">
          <input type="number" className={`${claseInput} w-24`} value={form.orden} onChange={cambiar('orden')} />
        </Campo>
        <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
          {editandoId ? 'Guardar cambios' : `Agregar ${titulo.toLowerCase()}`}
        </button>
        {editandoId && (
          <button type="button" onClick={cancelarEdicion} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        )}
      </form>

      {cargando && <Cargando />}

      {!cargando && items && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Nombre</th>
                <th className="px-4 py-2.5 font-medium">Orden</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
                <th className="px-4 py-2.5 font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="border-b border-linea last:border-0">
                  <td className="px-4 py-2.5 font-medium">{item.nombre}</td>
                  <td className="px-4 py-2.5">{item.orden}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={item.activo ? 'Activo' : 'Inactivo'} tono={item.activo ? 'cliente' : 'inactivo'} />
                  </td>
                  <td className="px-4 py-2.5 space-x-3">
                    <button type="button" onClick={() => editar(item)} className="text-sm text-acento hover:underline">
                      Editar
                    </button>
                    <button type="button" onClick={() => alternarEstado(item)} className="text-sm text-acento hover:underline">
                      {item.activo ? 'Desactivar' : 'Activar'}
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
