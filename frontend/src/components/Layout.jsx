import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const ICONOS = {
  embudo: <path d="M4 4h16l-6 8v6l-4 2v-8L4 4z" strokeWidth="1.6" strokeLinejoin="round" />,
  comercios: <path d="M4 10l1-6h14l1 6M4 10v10h16V10M4 10h16M9 20v-6h6v6" strokeWidth="1.6" strokeLinejoin="round" />,
  contactos: (
    <>
      <circle cx="12" cy="8" r="3.2" strokeWidth="1.6" />
      <path d="M5 20c0-3.9 3.1-6 7-6s7 2.1 7 6" strokeWidth="1.6" strokeLinecap="round" />
    </>
  ),
  productos: <path d="M4 7l8-4 8 4-8 4-8-4zm0 0v10l8 4m0-14v14m8-14v10l-8 4" strokeWidth="1.6" strokeLinejoin="round" />,
  oportunidades: (
    <path
      d="M12 3l2.6 5.9 6.4.6-4.8 4.3 1.4 6.2L12 16.9 6.4 20l1.4-6.2L3 9.5l6.4-.6L12 3z"
      strokeWidth="1.4"
      strokeLinejoin="round"
    />
  ),
  metricas: <path d="M4 20V10m6 10V4m6 16v-7m6 7V8" strokeWidth="1.6" strokeLinecap="round" />,
  usuarios: (
    <>
      <circle cx="9" cy="8" r="3" strokeWidth="1.6" />
      <path d="M3 20c0-3.3 2.7-5.5 6-5.5s6 2.2 6 5.5M16 8.5a2.6 2.6 0 110-5.2M15 14c2.8.3 4.5 2.2 4.5 5" strokeWidth="1.5" strokeLinecap="round" />
    </>
  ),
  configuracion: (
    <>
      <circle cx="12" cy="12" r="3" strokeWidth="1.6" />
      <path
        d="M19 12a7 7 0 00-.1-1.2l2-1.5-2-3.4-2.3.9a7 7 0 00-2-1.2L14 3h-4l-.6 2.6a7 7 0 00-2 1.2l-2.3-.9-2 3.4 2 1.5A7 7 0 005 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.4 2.3-.9c.6.5 1.3.9 2 1.2L10 21h4l.6-2.6c.7-.3 1.4-.7 2-1.2l2.3.9 2-3.4-2-1.5c.1-.4.1-.8.1-1.2z"
        strokeWidth="1.3"
        strokeLinejoin="round"
      />
    </>
  ),
  distribuidoras: (
    <path d="M4 21V8l8-5 8 5v13M9 21v-6h6v6M4 21h16" strokeWidth="1.6" strokeLinejoin="round" />
  ),
}

function seccionesPara(rol) {
  if (rol === 'ADMIN') {
    return [
      { to: '/distribuidoras', texto: 'Distribuidoras', icono: ICONOS.distribuidoras },
      { to: '/metricas', texto: 'Métricas', icono: ICONOS.metricas },
    ]
  }

  const secciones = [
    { to: '/embudo', texto: 'Embudo', icono: ICONOS.embudo },
    { to: '/comercios', texto: 'Comercios', icono: ICONOS.comercios },
    { to: '/contactos', texto: 'Contactos', icono: ICONOS.contactos },
    { to: '/productos', texto: 'Productos', icono: ICONOS.productos },
    { to: '/oportunidades', texto: 'Oportunidades', icono: ICONOS.oportunidades },
    { to: '/metricas', texto: 'Métricas', icono: ICONOS.metricas },
  ]

  if (rol === 'ADMIN_COMERCIO') {
    secciones.push({ to: '/usuarios', texto: 'Usuarios', icono: ICONOS.usuarios })
    secciones.push({ to: '/configuracion', texto: 'Configuración', icono: ICONOS.configuracion })
  }

  return secciones
}

export default function Layout() {
  const { usuario, logout, etiqueta } = useAuth()
  const navegar = useNavigate()
  const secciones = seccionesPara(usuario?.rol)

  function salir() {
    logout()
    navegar('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen bg-fondo">
      <aside className="flex w-56 shrink-0 flex-col border-r border-linea bg-superficie">
        <div className="border-b border-linea px-4 py-4">
          <p className="text-sm font-semibold leading-tight">CRM Ferretero</p>
        </div>

        <nav className="flex-1 space-y-0.5 p-2">
          {secciones.map((seccion) => (
            <NavLink
              key={seccion.to}
              to={seccion.to}
              className={({ isActive }) =>
                `flex items-center gap-2.5 rounded px-3 py-2 text-sm ${
                  isActive ? 'bg-acento/10 font-medium text-acento' : 'text-texto/80 hover:bg-fondo'
                }`
              }
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" className="h-[18px] w-[18px] shrink-0">
                {seccion.icono}
              </svg>
              {seccion.texto}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-linea p-3 text-sm">
          <p className="truncate font-medium">{usuario?.nombreCompleto}</p>
          <p className="truncate text-xs text-texto/50">{etiqueta('rol', usuario?.rol)}</p>
          <button
            type="button"
            onClick={salir}
            className="mt-2 w-full rounded border border-linea px-3 py-1.5 text-left text-xs text-texto/70 hover:bg-fondo"
          >
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-x-auto p-6">
        <Outlet />
      </main>
    </div>
  )
}
