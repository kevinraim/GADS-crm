import { useCallback, useEffect, useState } from 'react'
import { listarProductos } from '../../api/productos'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Vacio from '../../components/Vacio'
import Paginacion from '../../components/Paginacion'
import { formatMoneda } from '../../utils/formato'

export default function ProductosListado() {
  const { etiqueta, opcionesEnum } = useAuth()
  const [datos, setDatos] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const [q, setQ] = useState('')
  const [rubro, setRubro] = useState('')
  const [pagina, setPagina] = useState(0)

  const cargar = useCallback(() => {
    setCargando(true)
    listarProductos({ q: q || undefined, rubro: rubro || undefined, pagina, tamanio: 15 })
      .then(setDatos)
      .catch(setError)
      .finally(() => setCargando(false))
  }, [q, rubro, pagina])

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
      <h1 className="mb-4 text-lg font-semibold">Productos</h1>

      <div className="mb-4 flex flex-wrap gap-2">
        <input
          type="text"
          placeholder="Buscar por nombre, marca o código..."
          value={q}
          onChange={actualizarFiltro(setQ)}
          className="min-w-[240px] flex-1 rounded border border-linea bg-superficie px-3 py-1.5 text-sm"
        />
        <select value={rubro} onChange={actualizarFiltro(setRubro)} className="rounded border border-linea bg-superficie px-3 py-1.5 text-sm">
          <option value="">Todos los rubros</option>
          {opcionesEnum('rubroProducto').map((o) => (
            <option key={o.valor} value={o.valor}>
              {o.etiqueta}
            </option>
          ))}
        </select>
      </div>

      <MensajeError error={error} />

      {cargando && <Cargando />}

      {!cargando && datos && datos.contenido.length === 0 && <Vacio titulo="No se encontraron productos." />}

      {!cargando && datos && datos.contenido.length > 0 && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Código</th>
                <th className="px-4 py-2.5 font-medium">Producto</th>
                <th className="px-4 py-2.5 font-medium">Marca</th>
                <th className="px-4 py-2.5 font-medium">Rubro</th>
                <th className="px-4 py-2.5 font-medium">Presentación</th>
                <th className="px-4 py-2.5 text-right font-medium">Precio de referencia</th>
              </tr>
            </thead>
            <tbody>
              {datos.contenido.map((p) => (
                <tr key={p.id} className="border-b border-linea last:border-0 hover:bg-fondo/40">
                  <td className="px-4 py-2.5 text-texto/60">{p.codigo}</td>
                  <td className="px-4 py-2.5 font-medium">{p.nombre}</td>
                  <td className="px-4 py-2.5">{p.marca || '-'}</td>
                  <td className="px-4 py-2.5">{etiqueta('rubroProducto', p.rubro)}</td>
                  <td className="px-4 py-2.5">
                    {p.presentacion} ({etiqueta('unidadVenta', p.unidadVenta)})
                  </td>
                  <td className="numero px-4 py-2.5 text-right">{formatMoneda(p.precioListaReferencia)}</td>
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
