import client from './client'

export function listarUsuarios() {
  return client.get('/usuarios').then((r) => r.data)
}

export function listarUsuariosDeLaDistribuidora() {
  return client.get('/usuarios/administracion').then((r) => r.data)
}

export function crearUsuario(datos) {
  return client.post('/usuarios', datos).then((r) => r.data)
}

export function cambiarEstadoUsuario(id, activo) {
  return client.patch(`/usuarios/${id}/estado`, { activo }).then((r) => r.data)
}
