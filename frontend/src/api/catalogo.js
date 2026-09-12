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

export function listarTiposActividad() {
  return client.get('/tipos-actividad').then((r) => r.data)
}

// ---------------------------------------------------------------- administración (ADMIN_COMERCIO)

const RECURSOS = {
  etapas: '/etapas',
  origenes: '/origenes',
  motivos: '/motivos-perdida',
  tiposActividad: '/tipos-actividad',
}

export function listarCatalogoAdministracion(recurso) {
  return client.get(`${RECURSOS[recurso]}/administracion`).then((r) => r.data)
}

export function crearItemCatalogo(recurso, datos) {
  return client.post(RECURSOS[recurso], datos).then((r) => r.data)
}

export function actualizarItemCatalogo(recurso, id, datos) {
  return client.put(`${RECURSOS[recurso]}/${id}`, datos).then((r) => r.data)
}

export function cambiarEstadoItemCatalogo(recurso, id, activo) {
  return client.patch(`${RECURSOS[recurso]}/${id}/estado`, { activo }).then((r) => r.data)
}
