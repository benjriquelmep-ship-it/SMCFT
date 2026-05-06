// exception/GlobalExceptionHandler.java
// Intercepta TODAS las excepciones del Auth Service
// Devuelve JSON estructurado en vez de HTML de error

package com.example.AuthService.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
// @RestControllerAdvice aplica a todos los controllers del microservicio
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Errores de validación del LoginDTO
    // Se dispara cuando @Valid encuentra campos inválidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Error de validación en Auth Service: {}", errores);
        // HTTP 400 — los datos enviados son inválidos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Errores de lógica de negocio del AuthService
    // Ej: "Credenciales incorrectas", "El usuario está inactivo"
    //     "Token inválido", "Error al conectar con User Service"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarRuntime(
            RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Error en Auth Service: {}", ex.getMessage());
        // HTTP 401 Unauthorized — credenciales incorrectas o token inválido
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}