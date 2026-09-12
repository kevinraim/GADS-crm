import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarDistribuidoras, cambiarEstadoDistribuidora } from '../../api/distribuidoras'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Vacio from '../../components/Vacio'
import Etiqueta from '../../components/Etiqueta'

export default function DistribuidorasListado() {
  const [distribuidoras, setDistribuidoras] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    listarDistribuidoras().then(setDistribuidoras).catch(setError).finally(() => setCargando(false))
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  async function alternarEstado(distribuidora) {
    try {
      await cambiarEstadoDistribuidora(distribuidora.id, !distribuidora.activa)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-lg font-semibold">Distribuidoras</h1>
        <Link to="/distribuidoras/nueva" className="rounded bg-acento px-4 py-2 text-sm font-medium text-white">
          Nueva distribuidora
        </Link>
      </div>

      <MensajeError error={error} />

      {cargando && <Cargando />}

      {!cargando && distribuidoras && distribuidoras.length === 0 && (
        <Vacio titulo="Todavía no cargaste distribuidoras." subtitulo="Empezá por la primera." />
      )}

      {!cargando && distribuidoras && distribuidoras.length > 0 && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Distribuidora</th>
                <th className="px-4 py-2.5 font-medium">CUIT</th>
                <th className="px-4 py-2.5 font-medium">Email</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
                <th className="px-4 py-2.5 font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {distribuidoras.map((d) => (
                <tr key={d.id} className="border-b border-linea last:border-0 hover:bg-fondo/40">
                  <td className="px-4 py-2.5">
                    <Link to={`/distribuidoras/${d.id}/editar`} className="font-medium text-acento hover:underline">
                      {d.nombreVisible}
                    </Link>
                  </td>
                  <td className="px-4 py-2.5">{d.cuit || '-'}</td>
                  <td className="px-4 py-2.5">{d.email || '-'}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={d.activa ? 'Activa' : 'Inactiva'} tono={d.activa ? 'cliente' : 'inactivo'} />
                  </td>
                  <td className="px-4 py-2.5">
                    <button type="button" onClick={() => alternarEstado(d)} className="text-sm text-acento hover:underline">
                      {d.activa ? 'Desactivar' : 'Activar'}
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
