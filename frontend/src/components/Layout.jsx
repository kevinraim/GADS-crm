import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const SECCIONES = [
  {
    to: '/embudo',
    texto: 'Embudo',
    icono: (
      <path d="M4 4h16l-6 8v6l-4 2v-8L4 4z" strokeWidth="1.6" strokeLinejoin="round" />
    ),
  },
  {
    to: '/comercios',
    texto: 'Comercios',
    icono: <path d="M4 10l1-6h14l1 6M4 10v10h16V10M4 10h16M9 20v-6h6v6" strokeWidth="1.6" strokeLinejoin="round" />,
  },
  {
    to: '/contactos',
    texto: 'Contactos',
    icono: (
      <>
        <circle cx="12" cy="8" r="3.2" strokeWidth="1.6" />
        <path d="M5 20c0-3.9 3.1-6 7-6s7 2.1 7 6" strokeWidth="1.6" strokeLinecap="round" />
      </>
    ),
  },
  {
    to: '/productos',
    texto: 'Productos',
    icono: <path d="M4 7l8-4 8 4-8 4-8-4zm0 0v10l8 4m0-14v14m8-14v10l-8 4" strokeWidth="1.6" strokeLinejoin="round" />,
  },
  {
    to: '/oportunidades',
    texto: 'Oportunidades',
    icono: (
      <path
        d="M12 3l2.6 5.9 6.4.6-4.8 4.3 1.4 6.2L12 16.9 6.4 20l1.4-6.2L3 9.5l6.4-.6L12 3z"
        strokeWidth="1.4"
        strokeLinejoin="round"
      />
    ),
  },
]

export default function Layout() {
  const { usuario, logout, etiqueta } = useAuth()
  const navegar = useNavigate()

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
          {SECCIONES.map((seccion) => (
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
