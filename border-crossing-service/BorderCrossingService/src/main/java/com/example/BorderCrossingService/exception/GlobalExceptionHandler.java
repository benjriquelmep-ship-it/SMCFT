// Intercepta TODAS las excepciones del Border Crossing Service
// Convierte los errores en respuestas JSON claras para el cliente
// Sin este archivo los errores mostrarían mensajes técnicos difíciles de entender
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

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Para registrar los errores en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Errores de validación del DTO
    // Se dispara cuando @Valid encuentra campos inválidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        // Mapa vacío donde se guardan los errores
        Map<String, String> errores = new HashMap<>();

        // Recorre cada campo que falló y lo agrega al mapa
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        // Registra el error en la consola de IntelliJ
        log.error("Error de validación en Border Crossing: {}", errores);

        // HTTP 400 — los datos enviados son inválidos
        // Responde con el mapa de errores por campo
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Maneja los errores de lógica de negocio del Service
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