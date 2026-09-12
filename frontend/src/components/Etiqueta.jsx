const TONOS = {
  neutro: 'bg-linea/40 text-texto',
  cliente: 'bg-acento/10 text-acento',
  potencial: 'bg-linea/40 text-texto/70',
  inactivo: 'bg-texto/10 text-texto/50',
  abierta: 'bg-acento/10 text-acento',
  ganada: 'bg-ganada/10 text-ganada',
  perdida: 'bg-perdida/10 text-perdida',
}

// Mapea el VALOR crudo del enum (no el texto) a un tono visual. La etiqueta que se muestra
// siempre llega resuelta desde afuera vía useAuth().etiqueta(...).
export function tonoDeEstadoRegistro(valor) {
  switch (valor) {
    case 'CLIENTE':
      return 'cliente'
    case 'INACTIVO':
    case 'NO_CONTACTAR':
      return 'inactivo'
    default:
      return 'potencial'
  }
}

export function tonoDeEstadoOportunidad(valor) {
  switch (valor) {
    case 'GANADA':
      return 'ganada'
    case 'PERDIDA':
      return 'perdida'
    default:
      return 'abierta'
  }
}

export default function Etiqueta({ texto, tono = 'neutro' }) {
  return (
    <span className={`inline-flex items-center rounded px-2 py-0.5 text-xs font-medium ${TONOS[tono] || TONOS.neutro}`}>
      {texto}
    </span>
  )
}
