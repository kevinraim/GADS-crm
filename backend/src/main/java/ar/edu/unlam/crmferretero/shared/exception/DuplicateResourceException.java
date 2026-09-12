package ar.edu.unlam.crmferretero.shared.exception;

/** Se lanza cuando se intenta crear un recurso que ya existe (por ejemplo, CUIT repetido). Se traduce a HTTP 409. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }
}
