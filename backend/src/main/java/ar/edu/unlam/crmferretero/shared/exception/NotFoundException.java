package ar.edu.unlam.crmferretero.shared.exception;

/** Se lanza cuando un recurso solicitado por id no existe. Se traduce a HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}
