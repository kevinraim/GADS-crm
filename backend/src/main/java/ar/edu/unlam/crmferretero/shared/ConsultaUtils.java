package ar.edu.unlam.crmferretero.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Utilidades comunes para armar paginado y normalizar texto de formularios en los servicios. */
public final class ConsultaUtils {

    public static final int TAMANIO_POR_DEFECTO = 10;
    public static final int TAMANIO_MAXIMO = 100;

    private ConsultaUtils() {
    }

    /** Arma un Pageable seguro: página >= 0, tamaño entre 1 y TAMANIO_MAXIMO, orden por fecha de creación descendente. */
    public static Pageable paginar(Integer pagina, Integer tamanio) {
        int paginaSegura = (pagina == null || pagina < 0) ? 0 : pagina;
        int tamanioSeguro = (tamanio == null || tamanio <= 0)
                ? TAMANIO_POR_DEFECTO
                : Math.min(tamanio, TAMANIO_MAXIMO);
        return PageRequest.of(paginaSegura, tamanioSeguro, Sort.by(Sort.Direction.DESC, "creadoEn"));
    }

    /** Normaliza strings vacíos de formularios a null antes de guardar. */
    public static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }
}
