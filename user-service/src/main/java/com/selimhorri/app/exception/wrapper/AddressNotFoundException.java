package com.selimhorri.app.exception.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para cuando no se encuentra una dirección.
 * Devuelve automáticamente un código HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AddressNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public AddressNotFoundException() {
        super("La dirección solicitada no fue encontrada.");
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressNotFoundException(Throwable cause) {
        super("La dirección solicitada no fue encontrada.", cause);
    }
}
