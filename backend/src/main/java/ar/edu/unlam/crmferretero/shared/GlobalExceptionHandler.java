package ar.edu.unlam.crmferretero.shared;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.edu.unlam.crmferretero.shared.exception.BusinessRuleException;
import ar.edu.unlam.crmferretero.shared.exception.DuplicateResourceException;
import ar.edu.unlam.crmferretero.shared.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Única clase que traduce cualquier excepción que salga de un controlador al formato de ApiError.
 * Las respuestas 401/403 que dispara Spring Security antes de llegar acá se escriben a mano en
 * SecurityConfig, con el mismo formato, para que el front tenga un único contrato de error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> manejarNoEncontrado(NotFoundException ex, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> manejarReglaDeNegocio(BusinessRuleException ex, HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, "BUSINESS_RULE", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> manejarDuplicado(DuplicateResourceException ex, HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, "DUPLICATE", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Revisá los datos del formulario", request, errores);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> manejarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> manejarAccesoDenegado(AccessDeniedException ex, HttpServletRequest request) {
        return construir(HttpStatus.FORBIDDEN, "FORBIDDEN", "No tenés permisos para esta acción", request, null);
    }

    // Extensión razonable sobre la tabla de la especificación: credenciales inválidas en el login
    // deben responder 401, no un 500 genérico ni un 409 de regla de negocio.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> manejarAutenticacion(AuthenticationException ex, HttpServletRequest request) {
        return construir(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Email o contraseña incorrectos", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarError(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado. Probá de nuevo en unos minutos.", request, null);
    }

    private ResponseEntity<ApiError> construir(HttpStatus estado, String codigo, String mensaje,
                                                HttpServletRequest request, Map<String, String> erroresDeCampo) {
        ApiError cuerpo = new ApiError(Instant.now(), estado.value(), codigo, mensaje, request.getRequestURI(), erroresDeCampo);
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
