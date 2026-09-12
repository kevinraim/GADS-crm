import { useEffect, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { crearContacto, actualizarContacto, obtenerContacto } from '../../api/contactos'
import { opcionesEmpresas } from '../../api/empresas'
import { listarUsuarios } from '../../api/usuarios'
import { listarOrigenes } from '../../api/catalogo'
import Campo, { claseInput } from '../../components/Campo'
import MensajeError from '../../components/MensajeError'
import Cargando from '../../components/Cargando'

const VACIO = {
  nombre: '',
  apellido: '',
  documento: '',
  cargo: '',
  email: '',
  telefono: '',
  whatsapp: '',
  empresaId: '',
  responsableComercialId: '',
  origenId: '',
  observaciones: '',
}

const CARGOS_SUGERIDOS = ['Dueño', 'Encargado de compras', 'Jefe de depósito', 'Capataz de obra']

export default function ContactoFormulario() {
  const { id } = useParams()
  const [parametros] = useSearchParams()
  const esEdicion = !!id
  const navegar = useNavigate()

  const [form, setForm] = useState({ ...VACIO, empresaId: parametros.get('empresaId') || '' })
  const [empresas, setEmpresas] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [origenes, setOrigenes] = useState([])
  const [cargando, setCargando] = useState(esEdicion)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    opcionesEmpresas().then(setEmpresas).catch(() => {})
    listarUsuarios().then(setUsuarios).catch(() => {})
    listarOrigenes().then(setOrigenes).catch(() => {})
  }, [])

  useEffect(() => {
    if (!esEdicion) return
    obtenerContacto(id)
      .then((c) =>
        setForm({
          nombre: c.nombre || '',
          apellido: c.apellido || '',
          documento: c.documento || '',
          cargo: c.cargo || '',
          email: c.email || '',
          telefono: c.telefono || '',
          whatsapp: c.whatsapp || '',
          empresaId: c.empresaId || '',
          responsableComercialId: c.responsableComercialId || '',
          origenId: c.origenId || '',
          observaciones: c.observaciones || '',
        })
      )
      .catch(setError)
      .finally(() => setCargando(false))
  }, [id, esEdicion])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: evento.target.value }))
  }

  async function manejarEnvio(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      const guardado = esEdicion ? await actualizarContacto(id, form) : await crearContacto(form)
      navegar(`/contactos/${guardado.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  if (cargando) return <Cargando />

  return (
    <div className="max-w-2xl">
      <h1 className="mb-4 text-lg font-semibold">{esEdicion ? 'Editar contacto' : 'Nuevo contacto'}</h1>

      <MensajeError error={error} />

      <form onSubmit={manejarEnvio} className="space-y-6 rounded border border-linea bg-superficie p-6">
        <div className="grid grid-cols-2 gap-4">
          <Campo label="Nombre" required error={error?.erroresDeCampo?.nombre}>
            <input className={claseInput} value={form.nombre} onChange={cambiar('nombre')} required />
          </Campo>
          <Campo label="Apellido" required error={error?.erroresDeCampo?.apellido}>
            <input className={claseInput} value={form.apellido} onChange={cambiar('apellido')} required />
          </Campo>

          <Campo label="Cargo" error={error?.erroresDeCampo?.cargo}>
            <input className={claseInput} value={form.cargo} onChange={cambiar('cargo')} list="cargos-sugeridos" />
            <datalist id="cargos-sugeridos">
              {CARGOS_SUGERIDOS.map((c) => (
                <option key={c} value={c} />
              ))}
            </datalist>
          </Campo>
          <Campo label="Documento" error={error?.erroresDeCampo?.documento}>
            <input className={claseInput} value={form.documento} onChange={cambiar('documento')} />
          </Campo>

          <Campo label="Comercio" error={error?.erroresDeCampo?.empresaId}>
            <select className={claseInput} value={form.empresaId} onChange={cambiar('empresaId')}>
              <option value="">Sin comercio (cliente individual)</option>
              {empresas.map((e) => (
                <option key={e.valor} value={e.valor}>
                  {e.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Responsable comercial" error={error?.erroresDeCampo?.responsableComercialId}>
            <select className={claseInput} value={form.responsableComercialId} onChange={cambiar('responsableComercialId')}>
              <option value="">Sin asignar</option>
              {usuarios.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nombreCompleto}
                </option>
              ))}
            </select>
          </Campo>

          <Campo label="Email" error={error?.erroresDeCampo?.email}>
            <input type="email" className={claseInput} value={form.email} onChange={cambiar('email')} />
          </Campo>
          <Campo label="Teléfono" error={error?.erroresDeCampo?.telefono}>
            <input className={claseInput} value={form.telefono} onChange={cambiar('telefono')} />
          </Campo>

          <Campo label="WhatsApp" error={error?.erroresDeCampo?.whatsapp}>
            <input className={claseInput} value={form.whatsapp} onChange={cambiar('whatsapp')} />
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
        </div>

        <Campo label="Observaciones" error={error?.erroresDeCampo?.observaciones}>
          <textarea className={claseInput} rows={3} value={form.observaciones} onChange={cambiar('observaciones')} />
        </Campo>

        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {enviando ? 'Guardando...' : 'Guardar contacto'}
          </button>
          <button type="button" onClick={() => navegar(-1)} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
