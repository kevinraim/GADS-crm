import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { crearEmpresa, actualizarEmpresa, obtenerEmpresa } from '../../api/empresas'
import { listarUsuarios } from '../../api/usuarios'
import { listarOrigenes } from '../../api/catalogo'
import { useAuth } from '../../auth/AuthContext'
import Campo, { claseInput } from '../../components/Campo'
import MensajeError from '../../components/MensajeError'
import Cargando from '../../components/Cargando'

const VACIO = {
  razonSocial: '',
  nombreFantasia: '',
  cuit: '',
  email: '',
  telefono: '',
  direccion: '',
  localidad: '',
  sitioWeb: '',
  responsableComercialId: '',
  origenId: '',
  observaciones: '',
  tipoComercio: '',
  condicionIva: '',
  zonaReparto: '',
  condicionPagoHabitual: '',
  listaPrecios: '',
  limiteCreditoEstimado: '',
}

export default function ComercioFormulario() {
  const { id } = useParams()
  const esEdicion = !!id
  const navegar = useNavigate()
  const { opcionesEnum } = useAuth()

  const [form, setForm] = useState(VACIO)
  const [usuarios, setUsuarios] = useState([])
  const [origenes, setOrigenes] = useState([])
  const [cargando, setCargando] = useState(esEdicion)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    listarUsuarios().then(setUsuarios).catch(() => {})
    listarOrigenes().then(setOrigenes).catch(() => {})
  }, [])

  useEffect(() => {
    if (!esEdicion) return
    obtenerEmpresa(id)
      .then((empresa) =>
        setForm({
          razonSocial: empresa.razonSocial || '',
          nombreFantasia: empresa.nombreFantasia || '',
          cuit: empresa.cuit || '',
          email: empresa.email || '',
          telefono: empresa.telefono || '',
          direccion: empresa.direccion || '',
          localidad: empresa.localidad || '',
          sitioWeb: empresa.sitioWeb || '',
          responsableComercialId: empresa.responsableComercialId || '',
          origenId: empresa.origenId || '',
          observaciones: empresa.observaciones || '',
          tipoComercio: empresa.tipoComercio || '',
          condicionIva: empresa.condicionIva || '',
          zonaReparto: empresa.zonaReparto || '',
          condicionPagoHabitual: empresa.condicionPagoHabitual || '',
          listaPrecios: empresa.listaPrecios || '',
          limiteCreditoEstimado: empresa.limiteCreditoEstimado ?? '',
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

    const datos = {
      ...form,
      limiteCreditoEstimado: form.limiteCreditoEstimado === '' ? null : Number(form.limiteCreditoEstimado),
    }

    try {
      const guardada = esEdicion ? await actualizarEmpresa(id, datos) : await crearEmpresa(datos)
      navegar(`/comercios/${guardada.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  if (cargando) return <Cargando />

  return (
    <div className="max-w-3xl">
      <h1 className="mb-4 text-lg font-semibold">{esEdicion ? 'Editar comercio' : 'Nuevo comercio'}</h1>

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
          <Campo label="Tipo de comercio" error={error?.erroresDeCampo?.tipoComercio}>
            <select className={claseInput} value={form.tipoComercio} onChange={cambiar('tipoComercio')}>
              <option value="">Elegir...</option>
              {opcionesEnum('tipoComercio').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>

          <Campo label="Condición de IVA" error={error?.erroresDeCampo?.condicionIva}>
            <select className={claseInput} value={form.condicionIva} onChange={cambiar('condicionIva')}>
              <option value="">Elegir...</option>
              {opcionesEnum('condicionIva').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Zona de reparto" error={error?.erroresDeCampo?.zonaReparto}>
            <input className={claseInput} value={form.zonaReparto} onChange={cambiar('zonaReparto')} placeholder="Partido o localidad" />
          </Campo>

          <Campo label="Condición de pago habitual" error={error?.erroresDeCampo?.condicionPagoHabitual}>
            <select className={claseInput} value={form.condicionPagoHabitual} onChange={cambiar('condicionPagoHabitual')}>
              <option value="">Elegir...</option>
              {opcionesEnum('condicionPago').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>
          <Campo label="Lista de precios" error={error?.erroresDeCampo?.listaPrecios}>
            <select className={claseInput} value={form.listaPrecios} onChange={cambiar('listaPrecios')}>
              <option value="">Elegir...</option>
              {opcionesEnum('listaPrecios').map((o) => (
                <option key={o.valor} value={o.valor}>
                  {o.etiqueta}
                </option>
              ))}
            </select>
          </Campo>

          <Campo label="Límite de cuenta corriente estimado" error={error?.erroresDeCampo?.limiteCreditoEstimado}>
            <input
              type="number"
              className={claseInput}
              value={form.limiteCreditoEstimado}
              onChange={cambiar('limiteCreditoEstimado')}
              placeholder="Informativo"
            />
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
          <Campo label="Email" error={error?.erroresDeCampo?.email}>
            <input type="email" className={claseInput} value={form.email} onChange={cambiar('email')} />
          </Campo>

          <Campo label="Teléfono" error={error?.erroresDeCampo?.telefono}>
            <input className={claseInput} value={form.telefono} onChange={cambiar('telefono')} />
          </Campo>
          <Campo label="Sitio web" error={error?.erroresDeCampo?.sitioWeb}>
            <input className={claseInput} value={form.sitioWeb} onChange={cambiar('sitioWeb')} />
          </Campo>

          <Campo label="Dirección" error={error?.erroresDeCampo?.direccion}>
            <input className={claseInput} value={form.direccion} onChange={cambiar('direccion')} />
          </Campo>
          <Campo label="Localidad" error={error?.erroresDeCampo?.localidad}>
            <input className={claseInput} value={form.localidad} onChange={cambiar('localidad')} />
          </Campo>
        </div>

        <Campo label="Observaciones" error={error?.erroresDeCampo?.observaciones}>
          <textarea className={claseInput} rows={3} value={form.observaciones} onChange={cambiar('observaciones')} />
        </Campo>

        <div className="flex gap-2">
          <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
            {enviando ? 'Guardando...' : 'Guardar comercio'}
          </button>
          <button type="button" onClick={() => navegar(-1)} className="rounded border border-linea px-4 py-2 text-sm">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}
