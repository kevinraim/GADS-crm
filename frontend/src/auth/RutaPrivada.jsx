import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './AuthContext'
import Cargando from '../components/Cargando'

// roles: lista opcional de roles permitidos. Sin roles, alcanza con estar autenticado.
export default function RutaPrivada({ roles }) {
  const { estaAutenticado, cargando, tieneRol } = useAuth()

  if (cargando) {
    return <Cargando />
  }

  if (!estaAutenticado) {
    return <Navigate to="/login" replace />
  }

  if (roles && !tieneRol(...roles)) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}
