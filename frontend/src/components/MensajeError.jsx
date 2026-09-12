export default function MensajeError({ error }) {
  if (!error) return null
  return (
    <div className="mb-4 rounded border border-perdida/30 bg-perdida/10 px-4 py-3 text-sm text-perdida">
      {error.mensaje || 'Ocurrió un error inesperado.'}
    </div>
  )
}
