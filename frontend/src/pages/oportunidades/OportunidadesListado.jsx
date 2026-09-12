import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarOportunidades } from '../../api/oportunidades'
import { listarUsuarios } from '../../api/usuarios'
import { listarEtapas } from '../../api/catalogo'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Vacio from '../../components/Vacio'
import Paginacion from '../../components/Paginacion'
import Etiqueta, { tonoDeEstadoOportunidad } from '../../components/Etiqueta'
import { formatMoneda } from '../../utils/formato'

export default function OportunidadesListado() {
  const { etiqueta, opcionesEnum } = useAuth()
  const [datos, setDatos] = useState(null)
  const [usuarios, setUsuarios] = useState([])
  const [etapas, setEtapas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const [q, setQ] = useState('')
  const [etapaId, setEtapaId] = useState('')
  const [responsableId, setResponsableId] = useState('')
  const [estado, setEstado] = useState('')
  const [pagina, setPagina] = useState(0)

  const cargar = useCallback(() => {
    setCargando(true)
    listarOportunidades({
      q: q || undefined,
      etapaId: etapaId || undefined,
      responsableId: responsableId || undefined,
      estado: estado || undefined,
      pagina,
      tamanio: 10,
    })
      .then(setDatos)
      .catch(setError)
      .finally(() => setCargando(false))
  }, [q, etapaId, responsableId, estado, pagina])

  useEffect(() => {
    listarUsuarios().then(setUsuarios).catch(() => {})
    listarEtapas().then(setEtapas).catch(() => {})
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  function actualizarFiltro(setter) {
    return (evento) => {
      setPagina(0)
      setter(evento.target.value)
    }
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-lg font-semibold">Oportunidades</h1>
        <Link to="/oportunidades/nueva" className="rounded bg-acento px-4 py-2 text-sm font-medium text-white">
          Nueva oportunidad
        </Link>
      </div>

      <div className="mb-4 flex flex-wrap gap-2">
        <input
          type="text"
          placeholder="Buscar por título..."
          value={q}
          onChange={actualizarFiltro(setQ)}
          className="min-w-[220px] flex-1 rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
        />
        <select value={etapaId} onChange={actualizarFiltro(setEtapaId)} className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm">
          <option value="">Todas las etapas</option>
          {etapas.map((et) => (
            <option key={et.id} value={et.id}>
              {et.nombre}
            </option>
          ))}
        </select>
        <select value={estado} onChange={actualizarFiltro(setEstado)} className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm">
          <option value="">Todos los estados</option>
          {opcionesEnum('estadoOportunidad').map((o) => (
            <option key={o.valor} value={o.valor}>
              {o.etiqueta}
            </option>
          ))}
        </select>
        <select
          value={responsableId}
          onChange={actualizarFiltro(setResponsableId)}
          className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
        >
          <option value="">Todos los responsables</option>
          {usuarios.map((u) => (
            <option key={u.id} value={u.id}>
              {u.nombreCompleto}
            </option>
          ))}
        </select>
      </div>

      <MensajeError error={error} />

      {cargando && <Cargando />}

      {!cargando && datos && datos.contenido.length === 0 && (
        <Vacio titulo="Todavía no cargaste oportunidades." subtitulo="Empezá por la primera." />
      )}

      {!cargando && datos && datos.contenido.length > 0 && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Título</th>
                <th className="px-4 py-2.5 font-medium">Comercio / contacto</th>
                <th className="px-4 py-2.5 font-medium">Etapa</th>
                <th className="px-4 py-2.5 font-medium">Responsable</th>
                <th className="px-4 py-2.5 text-right font-medium">Valor estimado</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
              </tr>
            </thead>
            <tbody>
              {datos.contenido.map((o) => (
                <tr key={o.id} className="border-b border-linea last:border-0 hover:bg-fondo/40">
                  <td className="px-4 py-2.5">
                    <Link to={`/oportunidades/${o.id}`} className="font-medium text-acento hover:underline">
                      {o.titulo}
                    </Link>
                  </td>
                  <td className="px-4 py-2.5">{o.empresaNombre || o.contactoNombre || '-'}</td>
                  <td className="px-4 py-2.5">{o.etapaActualNombre}</td>
                  <td className="px-4 py-2.5">{o.responsableComercialNombre}</td>
                  <td className="numero px-4 py-2.5 text-right">{formatMoneda(o.valorEstimado)}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={etiqueta('estadoOportunidad', o.estado)} tono={tonoDeEstadoOportunidad(o.estado)} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <Paginacion pagina={datos.pagina} totalPaginas={datos.totalPaginas} onCambiar={setPagina} />
        </div>
      )}
    </div>
  )
}
