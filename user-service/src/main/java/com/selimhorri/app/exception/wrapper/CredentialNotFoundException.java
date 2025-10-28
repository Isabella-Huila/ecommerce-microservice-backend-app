package com.selimhorri.app.exception.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para cuando no se encuentra una credencial.
 * Devuelve automáticamente un código HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CredentialNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CredentialNotFoundException() {
        super("La credencial solicitada no fue encontrada.");
    }

    public CredentialNotFoundException(String message) {
        super(message);
    }

    public CredentialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialNotFoundException(Throwable cause) {
        super("La credencial solicitada no fue encontrada.", cause);
    }
}











