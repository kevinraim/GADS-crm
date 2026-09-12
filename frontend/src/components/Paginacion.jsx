export default function Paginacion({ pagina, totalPaginas, onCambiar }) {
  if (totalPaginas <= 1) return null

  return (
    <div className="flex items-center justify-between border-t border-linea px-4 py-3 text-sm">
      <button
        type="button"
        disabled={pagina <= 0}
        onClick={() => onCambiar(pagina - 1)}
        className="rounded border border-linea px-3 py-1.5 disabled:opacity-40"
      >
        Anterior
      </button>
      <span className="text-texto/60">
        Página {pagina + 1} de {totalPaginas}
      </span>
      <button
        type="button"
        disabled={pagina >= totalPaginas - 1}
        onClick={() => onCambiar(pagina + 1)}
        className="rounded border border-linea px-3 py-1.5 disabled:opacity-40"
      >
        Siguiente
      </button>
    </div>
  )
}
