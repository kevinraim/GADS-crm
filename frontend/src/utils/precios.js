// Calcula el precio unitario sugerido (editable) para un ítem de oportunidad: toma el precio
// especial de la lista de precios del comercio si existe, si no el de referencia, y le aplica el
// mejor descuento por cantidad que corresponda. El valor devuelto es solo una sugerencia inicial:
// el vendedor siempre puede sobreescribirlo a mano en el formulario.
export function calcularPrecioSugerido(producto, listaPrecios, cantidad) {
  if (!producto) return 0

  const precioPorLista = listaPrecios && producto.preciosPorLista ? producto.preciosPorLista[listaPrecios] : null
  const base = precioPorLista != null ? Number(precioPorLista) : Number(producto.precioListaReferencia || 0)

  const cantidadNumero = Number(cantidad || 0)
  const escalones = producto.escalonesDescuento || []
  const descuentoPorcentaje = escalones
    .filter((e) => cantidadNumero >= Number(e.cantidadMinima))
    .reduce((maximo, e) => Math.max(maximo, Number(e.descuentoPorcentaje)), 0)

  const precio = base * (1 - descuentoPorcentaje / 100)
  return Math.round(precio * 100) / 100
}
