export default function Vacio({ titulo, subtitulo }) {
  return (
    <div className="flex flex-col items-center justify-center gap-1 rounded border border-dashed border-linea bg-superficie px-6 py-12 text-center">
      <p className="font-medium text-texto">{titulo}</p>
      {subtitulo && <p className="text-sm text-texto/60">{subtitulo}</p>}
    </div>
  )
}
