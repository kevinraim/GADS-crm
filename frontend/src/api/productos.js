import client from './client'

export function listarProductos(params) {
  return client.get('/productos', { params }).then((r) => r.data)
}

export function opcionesProductos() {
  return client.get('/productos/opciones').then((r) => r.data)
}

export function obtenerProducto(id) {
  return client.get(`/productos/${id}`).then((r) => r.data)
}

export function crearProducto(datos) {
  return client.post('/productos', datos).then((r) => r.data)
}

export function actualizarProducto(id, datos) {
  return client.put(`/productos/${id}`, datos).then((r) => r.data)
}

export function cambiarEstadoProducto(id, activo) {
  return client.patch(`/productos/${id}/estado`, { activo }).then((r) => r.data)
}
