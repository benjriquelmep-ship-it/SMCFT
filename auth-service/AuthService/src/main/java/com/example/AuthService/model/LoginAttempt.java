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
@Schema(description = "Entidad del modelo físico que registra la traza de auditoría perimetral para cada intento de autenticación en el sistema")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del registro de intento de acceso", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 150)
    @Schema(
            description = "Identificador de usuario (email) con el que se pretendió realizar la autenticación",
            example = "fiscalizador@aduana.cl",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Column(nullable = false)
    @Schema(
            description = "Estado de resolución del intento: verdadero para accesos conformes, falso para credenciales rechazadas",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean exitoso;

    @Column(name = "ip_address", length = 45)
    @Schema(
            description = "Dirección de red IPv4 o IPv6 desde la cual se originó el socket de la petición HTTP",
            example = "192.168.1.45",
            maxLength = 45
    )
    private String ipAddress;

    @Column(name = "intento_at")
    @Schema(
            description = "Fecha y hora exacta del servidor en que se procesó el intento de autenticación",
            example = "2026-06-12T23:52:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime intentoAt;

    // RELACIÓN @ManyToOne — muchos intentos pueden estar asociados
    // a un token invalidado (el token del login exitoso que luego hizo logout)
    // nullable = true → no todos los intentos tienen un token asociado
    // Los intentos fallidos nunca tendrán token asociado
    @ManyToOne
    @JoinColumn(name = "token_blacklist_id", nullable = true)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Referencia al token revocado en lista negra asociado al ciclo de sesión del intento (si aplica)")
    private TokenBlacklist tokenBlacklist;
}