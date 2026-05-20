// Centraliza y captura los errores de toda la aplicación para responder con JSON limpio
package com.example.UserService.exception;

// @ControllerAdvice atrapa TODOS los errores de todos los controllers


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

    // Atrapa errores de validación del DTO
    // Se dispara cuando @Valid encuentra un campo inválido
    // Ej: email sin @, campo vacío, password muy corta, rol inválido
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        // Mapa con todos los errores de validación juntos
        // Clave = nombre del campo, Valor = mensaje de error del DTO
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Error de validación en User Service: {}", errores);
        // HTTP 400 Bad Request — los datos enviados son inválidos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Atrapa errores de lógica de negocio del Service
    // Ej: "Ya existe un usuario con ese RUT"
    //     "Usuario no encontrado con id: 999"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarRuntime(
            RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Error en User Service: {}", ex.getMessage());
        // HTTP 404 Not Found — recurso no encontrado o regla de negocio violada
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}