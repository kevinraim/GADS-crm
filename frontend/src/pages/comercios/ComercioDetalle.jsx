import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { obtenerEmpresa, cambiarEstadoEmpresa } from '../../api/empresas'
import { listarContactos } from '../../api/contactos'
import { listarOportunidades } from '../../api/oportunidades'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Vacio from '../../components/Vacio'
import Etiqueta, { tonoDeEstadoRegistro, tonoDeEstadoOportunidad } from '../../components/Etiqueta'
import HistorialComercial from '../../components/HistorialComercial'
import { formatMoneda } from '../../utils/formato'

export default function ComercioDetalle() {
  const { id } = useParams()
  const navegar = useNavigate()
  const { etiqueta, opcionesEnum } = useAuth()

  const [empresa, setEmpresa] = useState(null)
  const [contactos, setContactos] = useState([])
  const [oportunidades, setOportunidades] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = useCallback(() => {
    setCargando(true)
    Promise.all([
      obtenerEmpresa(id),
      listarContactos({ empresaId: id, tamanio: 50 }),
      listarOportunidades({ empresaId: id, tamanio: 50 }),
    ])
      .then(([e, c, o]) => {
        setEmpresa(e)
        setContactos(c.contenido)
        setOportunidades(o.contenido)
      })
      .catch(setError)
      .finally(() => setCargando(false))
  }, [id])

  useEffect(() => {
    cargar()
  }, [cargar])

  async function cambiarEstado(evento) {
    const nuevoEstado = evento.target.value
    if (!nuevoEstado) return
    try {
      await cambiarEstadoEmpresa(id, nuevoEstado)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  if (cargando) return <Cargando />
  if (!empresa) return null

  return (
    <div className="max-w-4xl">
      <div className="mb-4 flex items-start justify-between">
        <div>
          <h1 className="text-lg font-semibold">{empresa.nombreVisible}</h1>
          <p className="text-sm text-texto/60">{empresa.razonSocial}</p>
        </div>
        <div className="flex items-center gap-2">
          <select
            value={empresa.estado}
            onChange={cambiarEstado}
            className="rounded border border-linea bg-superficie px-2 py-1.5 text-sm"
          >
            {opcionesEnum('estadoRegistro').map((o) => (
              <option key={o.valor} value={o.valor}>
                {o.etiqueta}
              </option>
            ))}
          </select>
          <Link to={`/comercios/${id}/editar`} className="rounded border border-linea px-3 py-1.5 text-sm">
            Editar comercio
          </Link>
        </div>
      </div>

      <MensajeError error={error} />

      <div className="mb-6 grid grid-cols-2 gap-4 rounded border border-linea bg-superficie p-5 text-sm md:grid-cols-3">
        <Dato etiqueta="Estado">
          <Etiqueta texto={etiqueta('estadoRegistro', empresa.estado)} tono={tonoDeEstadoRegistro(empresa.estado)} />
        </Dato>
        <Dato etiqueta="Tipo de comercio">{etiqueta('tipoComercio', empresa.tipoComercio) || '-'}</Dato>
        <Dato etiqueta="Condición de IVA">{etiqueta('condicionIva', empresa.condicionIva) || '-'}</Dato>
        <Dato etiqueta="CUIT">{empresa.cuit || '-'}</Dato>
        <Dato etiqueta="Zona de reparto">{empresa.zonaReparto || '-'}</Dato>
        <Dato etiqueta="Condición de pago habitual">{etiqueta('condicionPago', empresa.condicionPagoHabitual) || '-'}</Dato>
        <Dato etiqueta="Lista de precios">{etiqueta('listaPrecios', empresa.listaPrecios) || '-'}</Dato>
        <Dato etiqueta="Límite de cta. cte. estimado">{formatMoneda(empresa.limiteCreditoEstimado)}</Dato>
        <Dato etiqueta="Responsable comercial">{empresa.responsableComercialNombre || '-'}</Dato>
        <Dato etiqueta="Origen">{empresa.origenNombre || '-'}</Dato>
        <Dato etiqueta="Email">{empresa.email || '-'}</Dato>
        <Dato etiqueta="Teléfono">{empresa.telefono || '-'}</Dato>
      </div>

      <section className="mb-6">
        <h2 className="mb-2 text-sm font-semibold text-texto/70">Contactos asociados</h2>
        {contactos.length === 0 ? (
          <Vacio titulo="Este comercio todavía no tiene contactos cargados." />
        ) : (
          <div className="divide-y divide-linea rounded border border-linea bg-superficie">
            {contactos.map((c) => (
              <Link key={c.id} to={`/contactos/${c.id}`} className="flex items-center justify-between px-4 py-2.5 text-sm hover:bg-fondo/40">
                <span className="font-medium text-acento">{c.nombreCompleto}</span>
                <span className="text-texto/60">{c.cargo || '-'}</span>
              </Link>
            ))}
          </div>
        )}
      </section>

      <section>
        <div className="mb-2 flex items-center justify-between">
          <h2 className="text-sm font-semibold text-texto/70">Oportunidades</h2>
          <button
            type="button"
            onClick={() => navegar(`/oportunidades/nueva?empresaId=${id}`)}
            className="text-sm text-acento hover:underline"
          >
            Nueva oportunidad
          </button>
        </div>
        {oportunidades.length === 0 ? (
          <Vacio titulo="Todavía no hay oportunidades para este comercio." />
        ) : (
          <div className="divide-y divide-linea rounded border border-linea bg-superficie">
            {oportunidades.map((o) => (
              <Link key={o.id} to={`/oportunidades/${o.id}`} className="flex items-center justify-between px-4 py-2.5 text-sm hover:bg-fondo/40">
                <span className="font-medium text-acento">{o.titulo}</span>
                <div className="flex items-center gap-3">
                  <span className="numero text-texto/70">{formatMoneda(o.valorEstimado)}</span>
                  <Etiqueta texto={etiqueta('estadoOportunidad', o.estado)} tono={tonoDeEstadoOportunidad(o.estado)} />
                </div>
              </Link>
            ))}
          </div>
        )}
      </section>

      <div className="mt-6">
        <HistorialComercial empresaId={id} />
      </div>
    </div>
  )
}

function Dato({ etiqueta, children }) {
  return (
    <div>
      <p className="text-xs uppercase text-texto/40">{etiqueta}</p>
      <p className="mt-0.5">{children}</p>
    </div>
  )
}
