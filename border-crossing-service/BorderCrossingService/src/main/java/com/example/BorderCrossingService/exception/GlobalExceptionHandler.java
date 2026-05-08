// exception/GlobalExceptionHandler.java
// Intercepta TODAS las excepciones del Border Crossing Service

package com.example.BorderCrossingService.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice aplica a todos los controllers del microservicio
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Errores de validación del DTO
    // Se dispara cuando @Valid encuentra campos inválidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Error de validación en Border Crossing: {}", errores);
        // HTTP 400 — los datos enviados son inválidos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Errores de lógica de negocio
    // Ej: vehículo no en territorio nacional, cruce no pendiente
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarRuntime(
            RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Error en Border Crossing Service: {}", ex.getMessage());
        // HTTP 404 — recurso no encontrado o regla de negocio violada
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}