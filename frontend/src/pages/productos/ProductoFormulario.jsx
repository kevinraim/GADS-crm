import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { crearProducto, actualizarProducto, obtenerProducto } from '../../api/productos'
import { useAuth } from '../../auth/AuthContext'
import Campo, { claseInput } from '../../components/Campo'
import MensajeError from '../../components/MensajeError'
import Cargando from '../../components/Cargando'

const VACIO = {
  codigo: '',
  nombre: '',
  marca: '',
  rubro: '',
  unidadVenta: '',
  presentacion: '',
  precioListaReferencia: '',
}

export default function ProductoFormulario() {
  const { id } = useParams()
  const esEdicion = !!id
  const navegar = useNavigate()
  const { opcionesEnum } = useAuth()
  const listasDePrecios = opcionesEnum('listaPrecios')

  const [form, setForm] = useState(VACIO)
  const [preciosPorLista, setPreciosPorLista] = useState({})
  const [escalones, setEscalones] = useState([])
  const [cargando, setCargando] = useState(esEdicion)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!esEdicion) return
    obtenerProducto(id)
      .then((p) => {
        setForm({
          codigo: p.codigo || '',
          nombre: p.nombre || '',
          marca: p.marca || '',
          rubro: p.rubro || '',
          unidadVenta: p.unidadVenta || '',
          presentacion: p.presentacion || '',
          precioListaReferencia: p.precioListaReferencia ?? '',
        })
        setPreciosPorLista(
          Object.fromEntries(Object.entries(p.preciosPorLista || {}).map(([lista, precio]) => [lista, precio ?? '']))
        )
        setEscalones((p.escalonesDescuento || []).map((e) => ({ cantidadMinima: e.cantidadMinima, descuentoPorcentaje: e.descuentoPorcentaje })))
      })
      .catch(setError)
      .finally(() => setCargando(false))
  }, [id, esEdicion])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: evento.target.value }))
  }

  function cambiarPrecioLista(lista) {
    return (evento) => setPreciosPorLista((p) => ({ ...p, [lista]: evento.target.value }))
  }

  function agregarEscalon() {
    setEscalones((e) => [...e, { cantidadMinima: '', descuentoPorcentaje: '' }])
  }

  function quitarEscalon(indice) {
    setEscalones((e) => e.filter((_, idx) => idx !== indice))
  }

  function cambiarEscalon(indice, campo, valor) {
    setEscalones((e) => e.map((esc, idx) => (idx === indice ? { ...esc, [campo]: valor } : esc)))
  }

  async function manejarEnvio(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)

    const datos = {
      ...form,
      precioListaReferencia: form.precioListaReferencia === '' ? null : Number(form.precioListaReferencia),
      preciosPorLista: Object.fromEntries(
        Object.entries(preciosPorLista)
          .filter(([, precio]) => precio !== '' && precio !== null && precio !== undefined)
          .map(([lista, precio]) => [lista, Number(precio)])
      ),
      escalonesDescuento: escalones
        .filter((e) => e.cantidadMinima !== '' && e.descuentoPorcentaje !== '')
        .map((e) => ({ cantidadMinima: Number(e.cantidadMinima), descuentoPorcentaje: Number(e.descuentoPorcentaje) })),
    }

    try {
      esEdicion ? await actualizarProducto(id, datos) : await crearProducto(datos)
      navegar('/productos')
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  if (cargando) return <Cargando />

  return (
    <div className="max-w-2xl">
      <h1 className="mb-4 text-lg font-semibold">{esEdicion ? 'Editar producto' : 'Nuevo producto'}</h1>

      <MensajeError error={error} />

      <form onSubmit={manejarEnvio} className="space-y-6 rounded border border-linea bg-superficie p-6">
        <div className="grid grid-cols-2 gap-4">
          <Campo label="Código" required error={error?.erroresDeCampo?.codigo}>
            <input className={claseInput} value={form.codigo} onChange={cambiar('codigo')} required />
          </Campo>
          <Campo label="Nombre" required error={error?.erroresDeCampo?.nombre}>
            <input className={claseInput} value={form.nombre} onChange={cambiar('nombre')} required />
          </Campo>
          <Campo label="Marca" error={error?.erroresDeCampo?.marca}>
            <input className={claseInput} value={form.marca} onChange={cambiar('marca')} />
          </Campo>
          <Campo label="Rubro" required error={error?.erroresDeCampo?.rubro}>
            <select className={claseInput} value={form.rubro} onChange={cambiar('rubro')} required>
              <option value="">Elegir...</option>
              {opcionesEnum('rubroProducto').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Unidad de venta" required error={error?.erroresDeCampo?.unidadVenta}>
            <select className={claseInput} value={form.unidadVenta} onChange={cambiar('unidadVenta')} required>
              <option value="">Elegir...</option>
              {opcionesEnum('unidadVenta').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Presentación" error={error?.erroresDeCampo?.presentacion}>
            <input className={claseInput} value={form.presentacion} onChange={cambiar('presentacion')} placeholder="Ej: caja x 10" />
          </Campo>
          <Campo label="Precio de lista de referencia" error={error?.erroresDeCampo?.precioListaReferencia}>
            <input type="number" min="0" step="0.01" className={claseInput} value={form.precioListaReferencia} onChange={cambiar('precioListaReferencia')} />
          </Campo>
        </div>

        <div>
          <h2 className="mb-1 text-sm font-semibold text-texto/70">Precios especiales por lista de precios</h2>
          <p className="mb-2 text-xs text-texto/50">
            Opcional. Las listas sin precio cargado acá usan el precio de referencia de arriba.
          </p>
          <div className="grid grid-cols-3 gap-3">
            {listasDePrecios.map((lista) => (
              <Campo key={lista.valor} label={lista.etiqueta}>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  className={claseInput}
                  placeholder="Precio de referencia"
                  value={preciosPorLista[lista.valor] ?? ''}
                  onChange={cambiarPrecioLista(lista.valor)}
                />
              </Campo>
            ))}
          </div>
        </div>

        <div>
          <div className="mb-1 flex items-center justify-between">
            <h2 className="text-sm font-semibold text-texto/70">Descuentos por cantidad</h2>
            <button type="button" onClick={agregarEscalon} className="text-sm text-acento hover:underline">
              + Agregar escalón
            </button>
          </div>
          <p className="mb-2 text-xs text-texto/50">
            Opcional. Si una cantidad alcanza varios escalones, se aplica el de mayor cantidad mínima.
          </p>

          {escalones.length === 0 && (
            <p className="rounded border border-dashed border-linea px-4 py-3 text-sm text-texto/50">Sin descuentos por cantidad.</p>
          )}

          {escalones.length > 0 && (
            <div className="space-y-2">
              {escalones.map((escalon, indice) => (
                <div key={indice} className="flex items-center gap-2 rounded border border-linea p-2">
                  <input
                    type="number"
                    min="0.01"
                    step="0.01"
                    className={`${claseInput} flex-1`}
                    placeholder="Cantidad mínima"
                    value={escalon.cantidadMinima}
                    onChange={(e) => cambiarEscalon(indice, 'cantidadMinima', e.target.value)}
                  />
                  <input
                    type="number"
                    min="0"
                    max="100"
                    step="0.01"
                    className={`${claseInput} flex-1`}
                    placeholder="Descuento %"
                    value={escalon.descuentoPorcentaje}
                    onChange={(e) => cambiarEscalon(indice, 'descuentoPorcentaje', e.target.value)}
                  />
                  <button type="button" onClick={() => quitarEscalon(indice)} className="px-2 text-sm text-perdida">
                    Quitar
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {enviando ? 'Guardando...' : 'Guardar producto'}
          </button>
          <button type="button" onClick={() => navegar('/productos')} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
