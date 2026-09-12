import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { crearOportunidad, actualizarOportunidad, obtenerOportunidad } from '../../api/oportunidades'
import { opcionesEmpresas, obtenerEmpresa } from '../../api/empresas'
import { listarContactos } from '../../api/contactos'
import { listarProductos } from '../../api/productos'
import { listarUsuarios } from '../../api/usuarios'
import { listarOrigenes } from '../../api/catalogo'
import { useAuth } from '../../auth/AuthContext'
import Campo, { claseInput } from '../../components/Campo'
import MensajeError from '../../components/MensajeError'
import Cargando from '../../components/Cargando'
import { formatMoneda } from '../../utils/formato'
import { calcularPrecioSugerido } from '../../utils/precios'

const DATOS_VACIOS = {
  titulo: '',
  empresaId: '',
  contactoId: '',
  responsableComercialId: '',
  origenId: '',
  valorEstimado: '',
  fechaEstimadaCierre: '',
  observaciones: '',
  condicionPagoNegociada: '',
  requiereAltaCuentaCorriente: false,
}

export default function OportunidadFormulario() {
  const { id } = useParams()
  const [parametros] = useSearchParams()
  const esEdicion = !!id
  const navegar = useNavigate()
  const { opcionesEnum } = useAuth()

  const [form, setForm] = useState({ ...DATOS_VACIOS, empresaId: parametros.get('empresaId') || '' })
  const [items, setItems] = useState([])
  const [empresas, setEmpresas] = useState([])
  const [empresaSeleccionada, setEmpresaSeleccionada] = useState(null)
  const [contactos, setContactos] = useState([])
  const [productos, setProductos] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [origenes, setOrigenes] = useState([])
  const [cargando, setCargando] = useState(esEdicion)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    opcionesEmpresas().then(setEmpresas).catch(() => {})
    listarProductos({ tamanio: 100 }).then((r) => setProductos(r.contenido)).catch(() => {})
    listarUsuarios().then(setUsuarios).catch(() => {})
    listarOrigenes().then(setOrigenes).catch(() => {})
  }, [])

  // Los contactos disponibles se acotan al comercio elegido (o se muestran todos si no hay ninguno).
  useEffect(() => {
    listarContactos({ empresaId: form.empresaId || undefined, tamanio: 100 })
      .then((r) => setContactos(r.contenido))
      .catch(() => {})
  }, [form.empresaId])

  // Se necesita la lista de precios del comercio (no viaja en las "opciones" del combo) para poder
  // sugerir el precio unitario de cada ítem.
  useEffect(() => {
    if (!form.empresaId) {
      setEmpresaSeleccionada(null)
      return
    }
    obtenerEmpresa(form.empresaId).then(setEmpresaSeleccionada).catch(() => setEmpresaSeleccionada(null))
  }, [form.empresaId])

  useEffect(() => {
    if (!esEdicion) return
    obtenerOportunidad(id)
      .then((o) => {
        setForm({
          titulo: o.titulo || '',
          empresaId: o.empresaId || '',
          contactoId: o.contactoId || '',
          responsableComercialId: o.responsableComercialId || '',
          origenId: o.origenId || '',
          valorEstimado: o.valorEstimado ?? '',
          fechaEstimadaCierre: o.fechaEstimadaCierre || '',
          observaciones: o.observaciones || '',
          condicionPagoNegociada: o.condicionPagoNegociada || '',
          requiereAltaCuentaCorriente: o.requiereAltaCuentaCorriente || false,
        })
        setItems(o.items.map((it) => ({ productoId: it.productoId, cantidad: it.cantidad, precioUnitario: it.precioUnitario })))
      })
      .catch(setError)
      .finally(() => setCargando(false))
  }, [id, esEdicion])

  function cambiar(campo) {
    return (evento) => {
      const valor = evento.target.type === 'checkbox' ? evento.target.checked : evento.target.value
      setForm((f) => ({ ...f, [campo]: valor }))
    }
  }

  function agregarItem() {
    setItems((i) => [...i, { productoId: '', cantidad: 1, precioUnitario: 0 }])
  }

  function quitarItem(indice) {
    setItems((i) => i.filter((_, idx) => idx !== indice))
  }

  function cambiarItem(indice, campo, valor) {
    setItems((i) =>
      i.map((item, idx) => {
        if (idx !== indice) return item

        if (campo === 'productoId') {
          const producto = productos.find((p) => p.id === valor)
          const precioSugerido = producto
            ? calcularPrecioSugerido(producto, empresaSeleccionada?.listaPrecios, item.cantidad)
            : item.precioUnitario
          return { ...item, productoId: valor, precioUnitario: precioSugerido }
        }

        if (campo === 'cantidad') {
          const producto = productos.find((p) => p.id === item.productoId)
          const precioSugerido = producto
            ? calcularPrecioSugerido(producto, empresaSeleccionada?.listaPrecios, valor)
            : item.precioUnitario
          return { ...item, cantidad: valor, precioUnitario: precioSugerido }
        }

        return { ...item, [campo]: valor }
      })
    )
  }

  const total = useMemo(
    () => items.reduce((acum, it) => acum + Number(it.cantidad || 0) * Number(it.precioUnitario || 0), 0),
    [items]
  )

  async function manejarEnvio(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)

    const datos = {
      ...form,
      empresaId: form.empresaId || null,
      contactoId: form.contactoId || null,
      origenId: form.origenId || null,
      fechaEstimadaCierre: form.fechaEstimadaCierre || null,
      valorEstimado: form.valorEstimado === '' ? null : Number(form.valorEstimado),
      condicionPagoNegociada: form.condicionPagoNegociada || null,
      items: items
        .filter((it) => it.productoId)
        .map((it) => ({ productoId: it.productoId, cantidad: Number(it.cantidad), precioUnitario: Number(it.precioUnitario) })),
    }

    try {
      const guardada = esEdicion ? await actualizarOportunidad(id, datos) : await crearOportunidad(datos)
      navegar(`/oportunidades/${guardada.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  if (cargando) return <Cargando />

  return (
    <div className="max-w-3xl">
      <h1 className="mb-4 text-lg font-semibold">{esEdicion ? 'Editar oportunidad' : 'Nueva oportunidad'}</h1>

      <MensajeError error={error} />

      <form onSubmit={manejarEnvio} className="space-y-6 rounded border border-linea bg-superficie p-6">
        <Campo label="Título" required error={error?.erroresDeCampo?.titulo}>
          <input className={claseInput} value={form.titulo} onChange={cambiar('titulo')} required />
        </Campo>

        <div className="grid grid-cols-2 gap-4">
          <Campo label="Comercio" error={error?.erroresDeCampo?.empresaId}>
            <select className={claseInput} value={form.empresaId} onChange={cambiar('empresaId')}>
              <option value="">Sin comercio</option>
              {empresas.map((e) => (
                <option key={e.valor} value={e.valor}>
                  {e.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Contacto" error={error?.erroresDeCampo?.contactoId}>
            <select className={claseInput} value={form.contactoId} onChange={cambiar('contactoId')}>
              <option value="">Sin contacto</option>
              {contactos.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombreCompleto}
                </option>
              ))}
            </select>
          </Campo>

          <Campo label="Responsable comercial" required error={error?.erroresDeCampo?.responsableComercialId}>
            <select className={claseInput} value={form.responsableComercialId} onChange={cambiar('responsableComercialId')} required>
              <option value="">Elegir...</option>
              {usuarios.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nombreCompleto}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Origen" error={error?.erroresDeCampo?.origenId}>
            <select className={claseInput} value={form.origenId} onChange={cambiar('origenId')}>
              <option value="">Sin asignar</option>
              {origenes.map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>

          <Campo label="Fecha estimada de cierre" error={error?.erroresDeCampo?.fechaEstimadaCierre}>
            <input type="date" className={claseInput} value={form.fechaEstimadaCierre} onChange={cambiar('fechaEstimadaCierre')} />
          </Campo>
          <Campo label="Condición de pago negociada" error={error?.erroresDeCampo?.condicionPagoNegociada}>
            <select className={claseInput} value={form.condicionPagoNegociada} onChange={cambiar('condicionPagoNegociada')}>
              <option value="">Sin especificar</option>
              {opcionesEnum('condicionPago').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
        </div>

        <label className="flex items-center gap-2 text-sm">
          <input type="checkbox" checked={form.requiereAltaCuentaCorriente} onChange={cambiar('requiereAltaCuentaCorriente')} />
          Requiere alta de cuenta corriente
        </label>

        <div>
          <div className="mb-2 flex items-center justify-between">
            <h2 className="text-sm font-semibold text-texto/70">Lista de materiales</h2>
            <button type="button" onClick={agregarItem} className="text-sm text-acento hover:underline">
              + Agregar ítem
            </button>
          </div>

          {items.length === 0 && (
            <p className="rounded border border-dashed border-linea px-4 py-4 text-sm text-texto/50">
              Sin ítems cargados. Si no agregás ninguno, se usa el valor estimado ingresado a mano.
            </p>
          )}

          {items.length > 0 && (
            <p className="mb-2 text-xs text-texto/50">
              El precio unitario se sugiere solo según la lista de precios del comercio
              {empresaSeleccionada?.listaPrecios ? ` (${empresaSeleccionada.listaPrecios})` : ' (sin lista asignada: usa el precio de referencia)'}
              {' '}y la cantidad (por si aplica un descuento por volumen) — siempre lo podés editar a mano.
            </p>
          )}

          {items.length > 0 && (
            <div className="space-y-2">
              {items.map((item, indice) => (
                <div key={indice} className="flex items-center gap-2 rounded border border-linea p-2">
                  <select
                    className={`${claseInput} flex-1`}
                    value={item.productoId}
                    onChange={(e) => cambiarItem(indice, 'productoId', e.target.value)}
                  >
                    <option value="">Elegir producto...</option>
                    {productos.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.nombre} ({p.presentacion})
                      </option>
                    ))}
                  </select>
                  <input
                    type="number"
                    min="0.01"
                    step="0.01"
                    className={`${claseInput} w-24`}
                    placeholder="Cantidad"
                    value={item.cantidad}
                    onChange={(e) => cambiarItem(indice, 'cantidad', e.target.value)}
                  />
                  <input
                    type="number"
                    min="0"
                    step="0.01"
                    className={`${claseInput} w-32`}
                    placeholder="Precio unit."
                    value={item.precioUnitario}
                    onChange={(e) => cambiarItem(indice, 'precioUnitario', e.target.value)}
                  />
                  <button type="button" onClick={() => quitarItem(indice)} className="px-2 text-sm text-perdida">
                    Quitar
                  </button>
                </div>
              ))}
              <p className="text-right text-sm font-medium">
                Total: <span className="numero">{formatMoneda(total)}</span>
              </p>
            </div>
          )}
        </div>

        {items.length === 0 && (
          <Campo label="Valor estimado" error={error?.erroresDeCampo?.valorEstimado}>
            <input type="number" min="0" step="0.01" className={claseInput} value={form.valorEstimado} onChange={cambiar('valorEstimado')} />
          </Campo>
        )}

        <Campo label="Observaciones" error={error?.erroresDeCampo?.observaciones}>
          <textarea className={claseInput} rows={3} value={form.observaciones} onChange={cambiar('observaciones')} />
        </Campo>

        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {enviando ? 'Guardando...' : 'Guardar oportunidad'}
          </button>
          <button type="button" onClick={() => navegar(-1)} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
