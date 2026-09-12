import client from './client'

export function login(email, password) {
  return client.post('/auth/login', { email, password }).then((r) => r.data)
}

export function obtenerUsuarioActual() {
  return client.get('/auth/me').then((r) => r.data)
}
