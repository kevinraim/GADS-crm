import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { obtenerContacto, cambiarEstadoContacto } from '../../api/contactos'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Etiqueta, { tonoDeEstadoRegistro } from '../../components/Etiqueta'

export default function ContactoDetalle() {
  const { id } = useParams()
  const { etiqueta, opcionesEnum } = useAuth()

  const [contacto, setContacto] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    obtenerContacto(id).then(setContacto).catch(setError).finally(() => setCargando(false))
  }, [id])

  useEffect(() => {
    cargar()
  }, [cargar])

  async function cambiarEstado(evento) {
    const nuevoEstado = evento.target.value
    if (!nuevoEstado) return
    try {
      await cambiarEstadoContacto(id, nuevoEstado)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  if (cargando) return <Cargando />
  if (!contacto) return null

  return (
    <div className="max-w-2xl">
      <div className="mb-4 flex items-start justify-between">
        <div>
          <h1 className="text-lg font-semibold">{contacto.nombreCompleto}</h1>
          <p className="text-sm text-texto/60">{contacto.cargo || 'Sin cargo asignado'}</p>
        </div>
        <div className="flex items-center gap-2">
          <select value={contacto.estado} onChange={cambiarEstado} className="rounded border border-linea bg-superficie px-2 py-1.5 text-sm">
            {opcionesEnum('estadoRegistro').map((o) => (
              <option key={o.valor} value={o.valor}>
                {o.etiqueta}
              </option>
            ))}
          </select>
          <Link to={`/contactos/${id}/editar`} className="rounded border border-linea px-3 py-1.5 text-sm">
            Editar contacto
          </Link>
        </div>
      </div>

      <MensajeError error={error} />

      <div className="grid grid-cols-2 gap-4 rounded border border-linea bg-superficie p-5 text-sm">
        <Dato titulo="Estado">
          <Etiqueta texto={etiqueta('estadoRegistro', contacto.estado)} tono={tonoDeEstadoRegistro(contacto.estado)} />
        </Dato>
        <Dato titulo="Documento">{contacto.documento || '-'}</Dato>
        <Dato titulo="Comercio">
          {contacto.empresaId ? (
            <Link to={`/comercios/${contacto.empresaId}`} className="text-acento hover:underline">
              {contacto.empresaNombre}
            </Link>
          ) : (
            'Cliente individual'
          )}
        </Dato>
        <Dato titulo="Responsable comercial">{contacto.responsableComercialNombre || '-'}</Dato>
        <Dato titulo="Email">{contacto.email || '-'}</Dato>
        <Dato titulo="Teléfono">{contacto.telefono || '-'}</Dato>
        <Dato titulo="WhatsApp">{contacto.whatsapp || '-'}</Dato>
        <Dato titulo="Origen">{contacto.origenNombre || '-'}</Dato>
      </div>

      {contacto.observaciones && (
        <div className="mt-4 rounded border border-linea bg-superficie p-4 text-sm">
          <p className="mb-1 text-xs uppercase text-texto/40">Observaciones</p>
          <p>{contacto.observaciones}</p>
        </div>
      )}
    </div>
  )
}

function Dato({ titulo, children }) {
  return (
    <div>
      <p className="text-xs uppercase text-texto/40">{titulo}</p>
      <p className="mt-0.5">{children}</p>
    </div>
  )
}
