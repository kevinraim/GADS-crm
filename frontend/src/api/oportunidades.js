import client from './client'

export function listarOportunidades(params) {
  return client.get('/oportunidades', { params }).then((r) => r.data)
}

export function obtenerOportunidad(id) {
  return client.get(`/oportunidades/${id}`).then((r) => r.data)
}

export function crearOportunidad(datos) {
  return client.post('/oportunidades', datos).then((r) => r.data)
}

export function actualizarOportunidad(id, datos) {
  return client.put(`/oportunidades/${id}`, datos).then((r) => r.data)
}

export function cambiarEtapaOportunidad(id, datos) {
  return client.patch(`/oportunidades/${id}/etapa`, datos).then((r) => r.data)
}

export function historialEtapasOportunidad(id) {
  return client.get(`/oportunidades/${id}/historial-etapas`).then((r) => r.data)
}
