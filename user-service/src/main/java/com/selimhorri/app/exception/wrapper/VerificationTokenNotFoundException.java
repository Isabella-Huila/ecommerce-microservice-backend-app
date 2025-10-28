package com.selimhorri.app.exception.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando un token de verificación
 * no se encuentra en el sistema. 
 * Retorna un código HTTP 404 (Not Found) en lugar de 500.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VerificationTokenNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VerificationTokenNotFoundException() {
        super("El token de verificación no fue encontrado.");
    }

    public VerificationTokenNotFoundException(String message) {
        super(message);
    }

    public VerificationTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenNotFoundException(Throwable cause) {
        super("El token de verificación no fue encontrado.", cause);
    }
}










