import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { crearDistribuidora, actualizarDistribuidora, obtenerDistribuidora } from '../../api/distribuidoras'
import Campo, { claseInput } from '../../components/Campo'
import MensajeError from '../../components/MensajeError'
import Cargando from '../../components/Cargando'

const VACIO = {
  razonSocial: '',
  nombreFantasia: '',
  cuit: '',
  email: '',
  telefono: '',
}

const ADMIN_VACIO = {
  nombre: '',
  apellido: '',
  email: '',
  password: '',
}

export default function DistribuidoraFormulario() {
  const { id } = useParams()
  const esEdicion = !!id
  const navegar = useNavigate()

  const [form, setForm] = useState(VACIO)
  const [admin, setAdmin] = useState(ADMIN_VACIO)
  const [cargando, setCargando] = useState(esEdicion)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!esEdicion) return
    obtenerDistribuidora(id)
      .then((d) =>
        setForm({
          razonSocial: d.razonSocial || '',
          nombreFantasia: d.nombreFantasia || '',
          cuit: d.cuit || '',
          email: d.email || '',
          telefono: d.telefono || '',
        })
      )
      .catch(setError)
      .finally(() => setCargando(false))
  }, [id, esEdicion])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: evento.target.value }))
  }

  function cambiarAdmin(campo) {
    return (evento) => setAdmin((a) => ({ ...a, [campo]: evento.target.value }))
  }

  async function manejarEnvio(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)

    const datos = { ...form, admin: esEdicion ? null : admin }

    try {
      const guardada = esEdicion ? await actualizarDistribuidora(id, datos) : await crearDistribuidora(datos)
      navegar(`/distribuidoras/${guardada.id}/editar`)
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  if (cargando) return <Cargando />

  return (
    <div className="max-w-2xl">
      <h1 className="mb-4 text-lg font-semibold">{esEdicion ? 'Editar distribuidora' : 'Nueva distribuidora'}</h1>

      <MensajeError error={error} />

      <form onSubmit={manejarEnvio} className="space-y-6 rounded border border-linea bg-superficie p-6">
        <div className="grid grid-cols-2 gap-4">
          <Campo label="Razón social" required error={error?.erroresDeCampo?.razonSocial}>
            <input className={claseInput} value={form.razonSocial} onChange={cambiar('razonSocial')} required />
          </Campo>
          <Campo label="Nombre de fantasía" error={error?.erroresDeCampo?.nombreFantasia}>
            <input className={claseInput} value={form.nombreFantasia} onChange={cambiar('nombreFantasia')} />
          </Campo>
          <Campo label="CUIT" error={error?.erroresDeCampo?.cuit}>
            <input className={claseInput} value={form.cuit} onChange={cambiar('cuit')} placeholder="Sin guiones, 11 dígitos" />
          </Campo>
          <Campo label="Email" error={error?.erroresDeCampo?.email}>
            <input type="email" className={claseInput} value={form.email} onChange={cambiar('email')} />
          </Campo>
          <Campo label="Teléfono" error={error?.erroresDeCampo?.telefono}>
            <input className={claseInput} value={form.telefono} onChange={cambiar('telefono')} />
          </Campo>
        </div>

        {!esEdicion && (
          <div>
            <h2 className="mb-2 text-sm font-semibold text-texto/70">Primer administrador de la distribuidora</h2>
            <div className="grid grid-cols-2 gap-4">
              <Campo label="Nombre" required error={error?.erroresDeCampo?.['admin.nombre']}>
                <input className={claseInput} value={admin.nombre} onChange={cambiarAdmin('nombre')} required />
              </Campo>
              <Campo label="Apellido" required error={error?.erroresDeCampo?.['admin.apellido']}>
                <input className={claseInput} value={admin.apellido} onChange={cambiarAdmin('apellido')} required />
              </Campo>
              <Campo label="Email" required error={error?.erroresDeCampo?.['admin.email']}>
                <input type="email" className={claseInput} value={admin.email} onChange={cambiarAdmin('email')} required />
              </Campo>
              <Campo label="Contraseña" required error={error?.erroresDeCampo?.['admin.password']}>
                <input type="password" className={claseInput} value={admin.password} onChange={cambiarAdmin('password')} required />
              </Campo>
            </div>
          </div>
        )}

        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {enviando ? 'Guardando...' : 'Guardar distribuidora'}
          </button>
          <button type="button" onClick={() => navegar('/distribuidoras')} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
