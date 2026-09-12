import client from './client'

export function listarContactos(params) {
  return client.get('/contactos', { params }).then((r) => r.data)
}

export function obtenerContacto(id) {
  return client.get(`/contactos/${id}`).then((r) => r.data)
}

export function crearContacto(datos) {
  return client.post('/contactos', datos).then((r) => r.data)
}

export function actualizarContacto(id, datos) {
  return client.put(`/contactos/${id}`, datos).then((r) => r.data)
}

export function cambiarEstadoContacto(id, estado) {
  return client.patch(`/contactos/${id}/estado`, { estado }).then((r) => r.data)
}
