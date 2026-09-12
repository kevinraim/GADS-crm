import client from './client'

export function listarDistribuidoras() {
  return client.get('/distribuidoras').then((r) => r.data)
}

export function obtenerDistribuidora(id) {
  return client.get(`/distribuidoras/${id}`).then((r) => r.data)
}

export function crearDistribuidora(datos) {
  return client.post('/distribuidoras', datos).then((r) => r.data)
}

export function actualizarDistribuidora(id, datos) {
  return client.put(`/distribuidoras/${id}`, datos).then((r) => r.data)
}

export function cambiarEstadoDistribuidora(id, activo) {
  return client.patch(`/distribuidoras/${id}/estado`, { activo }).then((r) => r.data)
}
