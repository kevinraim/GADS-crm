import client from './client'

export function listarUsuarios() {
  return client.get('/usuarios').then((r) => r.data)
}
