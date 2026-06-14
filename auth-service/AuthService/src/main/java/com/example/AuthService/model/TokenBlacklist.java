package com.example.AuthService.model;

// Representa la tabla "token_blacklist" en la base de datos auth_db
// Cuando un usuario cierra sesión su token se guarda aquí para invalidarlo
// Aunque el token no haya expirado, si está en esta tabla se rechaza

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token_blacklist")
@Entity
@Schema(description = "Entidad del modelo físico que actúa como bóveda de revocación para el almacenamiento y desestimación de tokens JWT invalidados")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del registro de revocación", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(
            description = "Cadena original cifrada del JSON Web Token invalidado administrativamente por logout",
            example = "eyJhbGciOiJIUzI1NiJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;

    @Column(nullable = false, length = 150)
    @Schema(
            description = "Correo electrónico del usuario titular de la sesión que fue revocada de forma anticipada",
            example = "fiscalizador@aduana.cl",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Column(name = "invalidado_at")
    @Schema(
            description = "Fecha y hora exacta del servidor en que el usuario solicitó el cierre de sesión voluntario",
            example = "2026-06-12T23:55:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime invalidadoAt;

    // RELACIÓN @OneToMany — un token invalidado puede tener
    // asociados varios intentos de login
    @OneToMany(mappedBy = "tokenBlacklist", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección de intentos de login que operaron bajo el ciclo de vida o vigencia de este identificador")
    private List<LoginAttempt> intentos;
}