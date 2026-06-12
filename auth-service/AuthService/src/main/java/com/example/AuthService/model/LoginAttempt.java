package com.example.AuthService.model;

// Registra cada intento de login en el sistema
// Tiene una relación @ManyToOne con TokenBlacklist
// Permite detectar ataques de fuerza bruta

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "login_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa login attempt")
public class LoginAttempt {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // Email con el que se intentó hacer login
    @Column(nullable = false, length = 150)
    @Schema(description = "Email", example = "usuario@ejemplo.cl", maxLength = 150)
    private String email;

    // true = login exitoso, false = login fallido
    @Column(nullable = false)
    @Schema(description = "Exitoso", example = "true")
    private Boolean exitoso;

    // IP desde donde se intentó el login
    // nullable porque no siempre está disponible
    @Column(name = "ip_address", length = 45)
    @Schema(description = "Ip Address", example = "ejemplo", maxLength = 45)
    private String ipAddress;

    // Fecha y hora exacta del intento
    @Column(name = "intento_at")
    @Schema(description = "Intento At", example = "2024-01-15")
    private LocalDateTime intentoAt;

    // RELACIÓN @ManyToOne — muchos intentos pueden estar asociados
    // a un token invalidado (el token del login exitoso que luego hizo logout)
    // nullable = true → no todos los intentos tienen un token asociado
    // Los intentos fallidos nunca tendrán token asociado
    @ManyToOne
    @JoinColumn(name = "token_blacklist_id", nullable = true)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private TokenBlacklist tokenBlacklist;
}
