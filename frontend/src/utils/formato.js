const formateadorMoneda = new Intl.NumberFormat('es-AR', {
  style: 'currency',
  currency: 'ARS',
  maximumFractionDigits: 0,
})

export function formatMoneda(valor) {
  if (valor === null || valor === undefined) return '-'
  return formateadorMoneda.format(valor)
}

export function formatFecha(fecha) {
  if (!fecha) return '-'
  const soloFecha = fecha.length === 10 ? `${fecha}T00:00:00` : fecha
  const date = new Date(soloFecha)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleDateString('es-AR')
}

export function formatFechaHora(fechaHora) {
  if (!fechaHora) return '-'
  const date = new Date(fechaHora)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleString('es-AR')
}
