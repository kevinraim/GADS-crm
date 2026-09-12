package ar.edu.unlam.crmferretero.shared;

import java.util.List;

/** Formato único de respuesta paginada para todos los listados de la API. */
public record PageResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas
) {

    public static <T> PageResponse<T> de(List<T> contenido, int pagina, int tamanio, long totalElementos) {
        int totalPaginas = tamanio <= 0 ? 0 : (int) Math.ceil((double) totalElementos / (double) tamanio);
        return new PageResponse<>(contenido, pagina, tamanio, totalElementos, totalPaginas);
    }
}
