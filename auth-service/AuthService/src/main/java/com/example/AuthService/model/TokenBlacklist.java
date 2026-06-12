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
@Schema(description = "Entidad que representa token blacklist")
public class TokenBlacklist {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1")
    private Long id;

    // El token JWT completo que fue invalidado
    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    // Email del usuario que cerró sesión
    // Útil para saber qué usuario invalidó qué token y cuándo
    @Column(nullable = false, length = 150)
    @Schema(description = "Email", example = "usuario@ejemplo.cl", maxLength = 150)
    private String email;

    // Fecha y hora exacta del cierre de sesión
    @Column(name = "invalidado_at")
    @Schema(description = "Invalidado At", example = "2024-01-15")
    private LocalDateTime invalidadoAt;

    // RELACIÓN @OneToMany — un token invalidado puede tener
    // asociados varios intentos de login
    @OneToMany(mappedBy = "tokenBlacklist", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<LoginAttempt> intentos;
}
