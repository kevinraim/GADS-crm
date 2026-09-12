import { Navigate, Route, Routes } from 'react-router-dom'
import RutaPrivada from './auth/RutaPrivada'
import Layout from './components/Layout'
import { useAuth } from './auth/AuthContext'
import Login from './pages/Login'
import Embudo from './pages/Embudo'
import Metricas from './pages/Metricas'
import ComerciosListado from './pages/comercios/ComerciosListado'
import ComercioFormulario from './pages/comercios/ComercioFormulario'
import ComercioDetalle from './pages/comercios/ComercioDetalle'
import ContactosListado from './pages/contactos/ContactosListado'
import ContactoFormulario from './pages/contactos/ContactoFormulario'
import ContactoDetalle from './pages/contactos/ContactoDetalle'
import ProductosListado from './pages/productos/ProductosListado'
import ProductoFormulario from './pages/productos/ProductoFormulario'
import OportunidadesListado from './pages/oportunidades/OportunidadesListado'
import OportunidadFormulario from './pages/oportunidades/OportunidadFormulario'
import OportunidadDetalle from './pages/oportunidades/OportunidadDetalle'
import DistribuidorasListado from './pages/distribuidoras/DistribuidorasListado'
import DistribuidoraFormulario from './pages/distribuidoras/DistribuidoraFormulario'
import UsuariosListado from './pages/usuarios/UsuariosListado'
import Configuracion from './pages/configuracion/Configuracion'

const ROLES_OPERATIVOS = ['ADMIN_COMERCIO', 'VENDEDOR', 'RESPONSABLE_COMERCIAL']

function InicioSegunRol() {
  const { tieneRol } = useAuth()
  return <Navigate to={tieneRol('ADMIN') ? '/distribuidoras' : '/embudo'} replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<RutaPrivada />}>
        <Route element={<Layout />}>
          <Route path="/" element={<InicioSegunRol />} />
          <Route path="/metricas" element={<Metricas />} />

          <Route element={<RutaPrivada roles={['ADMIN']} />}>
            <Route path="/distribuidoras" element={<DistribuidorasListado />} />
            <Route path="/distribuidoras/nueva" element={<DistribuidoraFormulario />} />
            <Route path="/distribuidoras/:id/editar" element={<DistribuidoraFormulario />} />
          </Route>

          <Route element={<RutaPrivada roles={ROLES_OPERATIVOS} />}>
            <Route path="/embudo" element={<Embudo />} />

            <Route path="/comercios" element={<ComerciosListado />} />
            <Route path="/comercios/nuevo" element={<ComercioFormulario />} />
            <Route path="/comercios/:id/editar" element={<ComercioFormulario />} />
            <Route path="/comercios/:id" element={<ComercioDetalle />} />

            <Route path="/contactos" element={<ContactosListado />} />
            <Route path="/contactos/nuevo" element={<ContactoFormulario />} />
            <Route path="/contactos/:id/editar" element={<ContactoFormulario />} />
            <Route path="/contactos/:id" element={<ContactoDetalle />} />

            <Route path="/productos" element={<ProductosListado />} />

            <Route path="/oportunidades" element={<OportunidadesListado />} />
            <Route path="/oportunidades/nueva" element={<OportunidadFormulario />} />
            <Route path="/oportunidades/:id/editar" element={<OportunidadFormulario />} />
            <Route path="/oportunidades/:id" element={<OportunidadDetalle />} />
          </Route>

          <Route element={<RutaPrivada roles={['ADMIN_COMERCIO']} />}>
            <Route path="/productos/nuevo" element={<ProductoFormulario />} />
            <Route path="/productos/:id/editar" element={<ProductoFormulario />} />
            <Route path="/usuarios" element={<UsuariosListado />} />
            <Route path="/configuracion" element={<Configuracion />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
