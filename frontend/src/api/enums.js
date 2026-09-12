import client from './client'

export function obtenerEnums() {
  return client.get('/enums').then((r) => r.data)
}
