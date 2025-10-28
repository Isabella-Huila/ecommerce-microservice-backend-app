package com.selimhorri.app.exception.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para cuando un usuario no es encontrado en el sistema.
 * Retorna un código HTTP 404 (Not Found) en lugar de 500.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserObjectNotFoundException() {
        super("El usuario solicitado no fue encontrado.");
    }

    public UserObjectNotFoundException(String message) {
        super(message);
    }

    public UserObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserObjectNotFoundException(Throwable cause) {
        super("El usuario solicitado no fue encontrado.", cause);
    }
}











