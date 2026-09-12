import client from './client'

export function listarActividades(params) {
  return client.get('/actividades', { params }).then((r) => r.data)
}

export function crearActividad(datos) {
  return client.post('/actividades', datos).then((r) => r.data)
}
