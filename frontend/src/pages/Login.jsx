import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import Campo, { claseInput } from '../components/Campo'
import MensajeError from '../components/MensajeError'

export default function Login() {
  const { login } = useAuth()
  const navegar = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [enviando, setEnviando] = useState(false)

  async function manejarEnvio(evento) {
    evento.preventDefault()
    setError(null)
    setEnviando(true)
    try {
      await login(email, password)
      navegar('/embudo', { replace: true })
    } catch (err) {
      setError(err)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-fondo px-4">
      <div className="w-full max-w-sm rounded border border-linea bg-superficie p-8">
        <h1 className="mb-1 text-lg font-semibold">CRM Ferretero</h1>
        <p className="mb-6 text-sm text-texto/60">Iniciá sesión para continuar.</p>

        <MensajeError error={error} />

        <form onSubmit={manejarEnvio} className="space-y-4">
          <Campo label="Email" required>
            <input
              type="email"
              className={claseInput}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="username"
              required
            />
          </Campo>

          <Campo label="Contraseña" required error={error?.erroresDeCampo?.password}>
            <input
              type="password"
              className={claseInput}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </Campo>

          <button
            type="submit"
            disabled={enviando}
            className="w-full rounded bg-acento px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
          >
            {enviando ? 'Ingresando...' : 'Iniciar sesión'}
          </button>
        </form>
      </div>
    </div>
  )
}
