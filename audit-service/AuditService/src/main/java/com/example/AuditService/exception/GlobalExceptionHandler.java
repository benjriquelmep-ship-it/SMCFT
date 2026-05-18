// Este archivo maneja todos los errores del Audit Service
package com.example.AuditService.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Este método maneja los errores de validación de los DTOs
    // Se activa cuando un campo falla una validación como: @NotBlank, @NotNull..
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        // HashMap vacío donde se van a guardar los errores
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        // Registra el error en la consola de IntelliJ
        log.error("Error de validación en Audit Service: {}", errores);

        // Responde con HTTP 400 BAD REQUEST y el mapa de errores
        // El cliente ve exactamente qué campos fallaron y por qué
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Este método maneja los errores de lógica de negocio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarRuntime(
            RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Error en Audit Service: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}