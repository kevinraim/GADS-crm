import { Navigate, Route, Routes } from 'react-router-dom'
import RutaPrivada from './auth/RutaPrivada'
import Layout from './components/Layout'
import Login from './pages/Login'
import Embudo from './pages/Embudo'
import ComerciosListado from './pages/comercios/ComerciosListado'
import ComercioFormulario from './pages/comercios/ComercioFormulario'
import ComercioDetalle from './pages/comercios/ComercioDetalle'
import ContactosListado from './pages/contactos/ContactosListado'
import ContactoFormulario from './pages/contactos/ContactoFormulario'
import ContactoDetalle from './pages/contactos/ContactoDetalle'
import ProductosListado from './pages/productos/ProductosListado'
import OportunidadesListado from './pages/oportunidades/OportunidadesListado'
import OportunidadFormulario from './pages/oportunidades/OportunidadFormulario'
import OportunidadDetalle from './pages/oportunidades/OportunidadDetalle'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<RutaPrivada />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Navigate to="/embudo" replace />} />
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
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
