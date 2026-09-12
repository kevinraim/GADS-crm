package ar.edu.unlam.crmferretero.shared.exception;

/** Se lanza cuando una operación viola una regla de negocio. Se traduce a HTTP 409. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String mensaje) {
        super(mensaje);
    }
}
