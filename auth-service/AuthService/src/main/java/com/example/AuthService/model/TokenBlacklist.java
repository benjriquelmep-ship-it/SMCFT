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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token_blacklist")
@Entity
public class TokenBlacklist {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El token JWT completo que fue invalidado
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    // Email del usuario que cerró sesión
    // Útil para saber qué usuario invalidó qué token y cuándo
    @Column(nullable = false, length = 150)
    private String email;

    // Fecha y hora exacta del cierre de sesión
    @Column(name = "invalidado_at")
    private LocalDateTime invalidadoAt;

    // RELACIÓN @OneToMany — un token invalidado puede tener
    // asociados varios intentos de login
    @OneToMany(mappedBy = "tokenBlacklist", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<LoginAttempt> intentos;
}
