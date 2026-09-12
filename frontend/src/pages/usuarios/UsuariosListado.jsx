import { useCallback, useEffect, useState } from 'react'
import { listarUsuariosDeLaDistribuidora, crearUsuario, cambiarEstadoUsuario } from '../../api/usuarios'
import { useAuth } from '../../auth/AuthContext'
import Cargando from '../../components/Cargando'
import MensajeError from '../../components/MensajeError'
import Etiqueta from '../../components/Etiqueta'
import Campo, { claseInput } from '../../components/Campo'

const VACIO = { nombre: '', apellido: '', email: '', password: '', rol: 'VENDEDOR' }

export default function UsuariosListado() {
  const { etiqueta } = useAuth()
  const [usuarios, setUsuarios] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [form, setForm] = useState(VACIO)
  const [enviando, setEnviando] = useState(false)

  const cargar = useCallback(() => {
    setCargando(true)
    listarUsuariosDeLaDistribuidora().then(setUsuarios).catch(setError).finally(() => setCargando(false))
  }, [])

  useEffect(() => {
    cargar()
  }, [cargar])

  function cambiar(campo) {
    return (evento) => setForm((f) => ({ ...f, [campo]: evento.target.value }))
  }

  async function crear(evento) {
    evento.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      await crearUsuario(form)
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  async function alternarEstado(usuario) {
    try {
      await cambiarEstadoUsuario(usuario.id, !usuario.activo)
      cargar()
    } catch (err) {
      setError(err)
    }
  }

  return (
    <div className="max-w-3xl">
      <h1 className="mb-4 text-lg font-semibold">Usuarios</h1>

      <MensajeError error={error} />

      <form onSubmit={crear} className="mb-6 space-y-4 rounded border border-linea bg-superficie p-5">
        <h2 className="text-sm font-semibold text-texto/70">Nuevo vendedor o responsable comercial</h2>
        <div className="grid grid-cols-2 gap-3">
          <Campo label="Nombre" required>
            <input className={claseInput} value={form.nombre} onChange={cambiar('nombre')} required />
          </Campo>
          <Campo label="Apellido" required>
            <input className={claseInput} value={form.apellido} onChange={cambiar('apellido')} required />
          </Campo>
          <Campo label="Email" required error={error?.erroresDeCampo?.email}>
            <input type="email" className={claseInput} value={form.email} onChange={cambiar('email')} required />
          </Campo>
          <Campo label="Contraseña" required error={error?.erroresDeCampo?.password}>
            <input type="password" className={claseInput} value={form.password} onChange={cambiar('password')} required />
          </Campo>
          <Campo label="Rol" required>
            <select className={claseInput} value={form.rol} onChange={cambiar('rol')}>
              <option value="VENDEDOR">Vendedor de zona</option>
              <option value="RESPONSABLE_COMERCIAL">Responsable comercial</option>
            </select>
          </Campo>
        </div>
        <button type="submit" disabled={enviando} className="rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60">
          {enviando ? 'Creando...' : 'Crear usuario'}
        </button>
      </form>

      {cargando && <Cargando />}

      {!cargando && usuarios && (
        <div className="overflow-hidden rounded border border-linea bg-superficie">
          <table className="w-full text-sm">
            <thead className="border-b border-linea bg-fondo/60 text-left text-xs uppercase text-texto/50">
              <tr>
                <th className="px-4 py-2.5 font-medium">Nombre</th>
                <th className="px-4 py-2.5 font-medium">Email</th>
                <th className="px-4 py-2.5 font-medium">Rol</th>
                <th className="px-4 py-2.5 font-medium">Estado</th>
                <th className="px-4 py-2.5 font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((u) => (
                <tr key={u.id} className="border-b border-linea last:border-0">
                  <td className="px-4 py-2.5 font-medium">{u.nombreCompleto}</td>
                  <td className="px-4 py-2.5">{u.email}</td>
                  <td className="px-4 py-2.5">{etiqueta('rol', u.rol)}</td>
                  <td className="px-4 py-2.5">
                    <Etiqueta texto={u.activo ? 'Activo' : 'Inactivo'} tono={u.activo ? 'cliente' : 'inactivo'} />
                  </td>
                  <td className="px-4 py-2.5">
                    {(u.rol === 'VENDEDOR' || u.rol === 'RESPONSABLE_COMERCIAL') && (
                      <button type="button" onClick={() => alternarEstado(u)} className="text-sm text-acento hover:underline">
                        {u.activo ? 'Desactivar' : 'Activar'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
