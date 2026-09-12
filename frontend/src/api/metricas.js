import client from './client'

export function obtenerMetricas(params) {
  return client.get('/metricas', { params }).then((r) => r.data)
}
