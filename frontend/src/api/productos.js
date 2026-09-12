import client from './client'

export function listarProductos(params) {
  return client.get('/productos', { params }).then((r) => r.data)
}

export function opcionesProductos() {
  return client.get('/productos/opciones').then((r) => r.data)
}
