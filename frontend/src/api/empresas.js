import client from './client'

export function listarEmpresas(params) {
  return client.get('/empresas', { params }).then((r) => r.data)
}

export function opcionesEmpresas() {
  return client.get('/empresas/opciones').then((r) => r.data)
}

export function obtenerEmpresa(id) {
  return client.get(`/empresas/${id}`).then((r) => r.data)
}

export function crearEmpresa(datos) {
  return client.post('/empresas', datos).then((r) => r.data)
}

export function actualizarEmpresa(id, datos) {
  return client.put(`/empresas/${id}`, datos).then((r) => r.data)
}

export function cambiarEstadoEmpresa(id, estado) {
  return client.patch(`/empresas/${id}/estado`, { estado }).then((r) => r.data)
}
