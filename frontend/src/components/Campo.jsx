export default function Campo({ label, error, required, children }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-texto">
        {label}
        {required && <span className="text-perdida"> *</span>}
      </span>
      {children}
      {error && <span className="mt-1 block text-xs text-perdida">{error}</span>}
    </label>
  )
}

export const claseInput =
  'w-full rounded border border-linea bg-superficie px-3 py-2 text-sm text-texto focus:border-acento focus:outline-none focus:ring-1 focus:ring-acento'
