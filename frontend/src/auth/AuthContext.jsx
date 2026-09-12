import { createContext, useContext, useEffect, useState, useCallback } from 'react'
import * as authApi from '../api/auth'
import { obtenerEnums } from '../api/enums'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [enums, setEnums] = useState({})
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('crm_token')
    if (!token) {
      setCargando(false)
      return
    }
    Promise.all([authApi.obtenerUsuarioActual(), obtenerEnums()])
      .then(([usuarioActual, enumsDisponibles]) => {
        setUsuario(usuarioActual)
        setEnums(enumsDisponibles)
      })
      .catch(() => {
        localStorage.removeItem('crm_token')
      })
      .finally(() => setCargando(false))
  }, [])

  const login = useCallback(async (email, password) => {
    const respuesta = await authApi.login(email, password)
    localStorage.setItem('crm_token', respuesta.token)
    setUsuario(respuesta.usuario)
    const enumsDisponibles = await obtenerEnums()
    setEnums(enumsDisponibles)
    return respuesta.usuario
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('crm_token')
    setUsuario(null)
    setEnums({})
  }, [])

  // Devuelve la etiqueta legible de un valor de enum, resuelta desde /api/enums.
  // Nunca hay que traducir enums a mano con un switch en ningún componente.
  const etiqueta = useCallback(
    (categoria, valor) => {
      if (!valor) return ''
      const opciones = enums[categoria] || []
      const opcion = opciones.find((o) => o.valor === valor)
      return opcion ? opcion.etiqueta : valor
    },
    [enums]
  )

  const opcionesEnum = useCallback((categoria) => enums[categoria] || [], [enums])

  return (
    <AuthContext.Provider
      value={{ usuario, cargando, estaAutenticado: !!usuario, login, logout, etiqueta, opcionesEnum, enums }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
