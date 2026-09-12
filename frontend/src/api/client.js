import axios from 'axios'

const client = axios.create({
  baseURL: '/api',
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('crm_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('crm_token')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    // Normaliza cualquier error al formato de ApiError del backend, para que los
    // formularios siempre puedan leer erroresDeCampo sin lógica extra.
    const apiError = error.response?.data ?? {
      estado: 0,
      codigo: 'SIN_CONEXION',
      mensaje: 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.',
      erroresDeCampo: null,
    }

    return Promise.reject(apiError)
  }
)

export default client
