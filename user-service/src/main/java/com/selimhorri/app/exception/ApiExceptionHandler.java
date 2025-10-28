package com.selimhorri.app.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.selimhorri.app.exception.payload.ExceptionMsg;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    /**
     * Maneja excepciones de validación (campos incorrectos, formatos inválidos, etc.)
     */
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class,
        BindException.class
    })
    public ResponseEntity<ExceptionMsg> handleValidationException(Exception e) {
        log.error("❌ Error de validación: {}", e.getMessage(), e);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensaje = "Los datos enviados no son válidos.";
        if (e instanceof MethodArgumentNotValidException) {
            var fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
            if (fieldError != null) {
                mensaje = String.format("El campo '%s' %s", fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return buildResponseEntity(status, mensaje, e);
    }

    /**
     * Maneja excepciones cuando no se encuentra una entidad específica (404).
     */
    @ExceptionHandler({
        UserObjectNotFoundException.class,
        CredentialNotFoundException.class,
        VerificationTokenNotFoundException.class,
        AddressNotFoundException.class
    })
    public ResponseEntity<ExceptionMsg> handleNotFoundException(RuntimeException e) {
        log.warn("⚠️ Recurso no encontrado: {}", e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;

        String mensaje = e.getMessage() != null ? e.getMessage() : "Recurso no encontrado.";

        return buildResponseEntity(status, mensaje, e);
    }

    /**
     * Captura cualquier otra excepción no manejada y evita el error 500 genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMsg> handleGeneralException(Exception e) {
        log.error("💥 Error inesperado: ", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return buildResponseEntity(
                status,
                "Ha ocurrido un error interno en el servidor. Inténtelo nuevamente más tarde.",
                e
        );
    }

    /**
     * Método auxiliar para construir respuestas JSON uniformes
     */
    private ResponseEntity<ExceptionMsg> buildResponseEntity(HttpStatus status, String mensaje, Throwable e) {
        ExceptionMsg body = ExceptionMsg.builder()
                .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                .status(status.value())
                .error(status.getReasonPhrase())
                .msg(mensaje)
                .detalleError(e.getClass().getSimpleName())
                .build();

        return new ResponseEntity<>(body, status);
    }
}



