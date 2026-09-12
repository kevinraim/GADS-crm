import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './AuthContext'
import Cargando from '../components/Cargando'

export default function RutaPrivada() {
  const { estaAutenticado, cargando } = useAuth()

  if (cargando) {
    return <Cargando />
  }

  if (!estaAutenticado) {
    return <Navigate to="/login" replace />
  }

  return <Outlet />
}
