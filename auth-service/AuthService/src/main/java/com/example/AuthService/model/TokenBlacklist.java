package com.example.AuthService.model;

// Representa la tabla "token_blacklist" en la base de datos smcft_auth
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
    // columnDefinition = "TEXT" porque los tokens son muy largos
    // VARCHAR tiene límite de 255 caracteres y un JWT puede superarlo
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    // Email del usuario que cerró sesión
    // Útil para saber qué usuario invalidó qué token y cuándo
    @Column(nullable = false, length = 150)
    private String email;

    // Fecha y hora exacta del cierre de sesión
    // Se asigna en el Service con LocalDateTime.now() al momento de invalidar
    @Column(name = "invalidado_at")
    private LocalDateTime invalidadoAt;

    // RELACIÓN @OneToMany — un token invalidado puede tener
    // asociados varios intentos de login
    // mappedBy = "tokenBlacklist" → LoginAttempt es el dueño de la relación
    // fetch = LAZY → los intentos se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "tokenBlacklist", fetch = FetchType.LAZY)
    // @JsonManagedReference evita ciclo infinito en la serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<LoginAttempt> intentos;
}
