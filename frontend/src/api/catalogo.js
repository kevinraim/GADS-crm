import client from './client'

export function listarEtapas() {
  return client.get('/etapas').then((r) => r.data)
}

export function listarOrigenes() {
  return client.get('/origenes').then((r) => r.data)
}

export function listarMotivosPerdida() {
  return client.get('/motivos-perdida').then((r) => r.data)
}
