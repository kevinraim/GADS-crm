import client from './client'

export function obtenerEmbudo(params) {
  return client.get('/embudo', { params }).then((r) => r.data)
}
