import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarEmpresas } from '../../api/empresas'
import { listarUsuarios } from '../../api/usuarios'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Vacio from '../../components/Vacio'
import Paginacion from '../../components/Paginacion'
import Etiqueta, { tonoDeEstadoRegistro } from '../../components/Etiqueta'

export default function ComerciosListado() {
  const { etiqueta, opcionesEnum } = useAuth()
  const [datos, setDatos] = useState(null)
  const [usuarios, setUsuarios] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const [q, setQ] = useState('')
  const [estado, setEstado] = useState('')
  const [tipoComercio, setTipoComercio] = useState('')
  const [responsableId, setResponsableId] = useState('')
  const [pagina, setPagina] = useState(0)

  const cargar = useCallback(() => {
    setCargando(true)
    listarEmpresas({
      q: q || undefined,
      estado: estado || undefined,
      tipoComercio: tipoComercio || undefined,
      responsableId: responsableId || undefined,
      pagina,
      tamanio: 10,
    })
      .then(setDatos)
      .catch(setError)
      .finally(() => setCargando(false))
  }, [q, estado, tipoComercio, responsableId, pagina])

  useEffect(() => {
    listarUsuarios().then(setUsuarios).catch(() => {})
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
        <h1 className="text-lg font-semibold">Comercios</h1>
        <Link to="/comercios/nuevo" className="rounded bg-acento px-4 py-2 text-sm font-medium text-white">
          Nuevo comercio
        </Link>
      </div>

      <div className="mb-4 flex flex-wrap gap-2">
        <input
          type="text"
          placeholder="Buscar por razón social, fantasía o CUIT..."
          value={q}
          onChange={actualizarFiltro(setQ)}
          className="min-w-[240px] flex-1 rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
        />
        <select value={estado} onChange={actualizarFiltro(setEstado)} className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm">
          <option value="">Todos los estados</option>
          {opcionesEnum('estadoRegistro').map((o) => (
            <option key={o.valor} value={o.valor}>
              {o.etiqueta}
            </option>
          ))}
        </select>
        <select
          value={tipoComercio}
          onChange={actualizarFiltro(setTipoComercio)}
          className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
        >
          <option value="">Todos los tipos</option>
          {opcionesEnum('tipoComercio').map((o) => (
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
        <Vacio titulo="Todavía no cargaste comercios." subtitulo="Empezá por el primero." />
      )}

      {!cargando && datos && datos.contenido.length > 0 && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Comercio</th>
                <th className="px-4 py-2.5 font-medium">Tipo</th>
                <th className="px-4 py-2.5 font-medium">Zona</th>
                <th className="px-4 py-2.5 font-medium">Responsable</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
              </tr>
            </thead>
            <tbody>
              {datos.contenido.map((empresa) => (
                <tr key={empresa.id} className="border-b border-linea last:border-0 hover:bg-fondo/40">
                  <td className="px-4 py-2.5">
                    <Link to={`/comercios/${empresa.id}`} className="font-medium text-acento hover:underline">
                      {empresa.nombreVisible}
                    </Link>
                  </td>
                  <td className="px-4 py-2.5">{etiqueta('tipoComercio', empresa.tipoComercio)}</td>
                  <td className="px-4 py-2.5">{empresa.zonaReparto || '-'}</td>
                  <td className="px-4 py-2.5">{empresa.responsableComercialNombre || '-'}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={etiqueta('estadoRegistro', empresa.estado)} tono={tonoDeEstadoRegistro(empresa.estado)} />
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
