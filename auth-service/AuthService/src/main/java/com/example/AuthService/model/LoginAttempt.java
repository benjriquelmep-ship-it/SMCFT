package com.example.AuthService.model;

// Registra cada intento de login en el sistema
// Tiene una relación @ManyToOne con TokenBlacklist
// Permite detectar ataques de fuerza bruta

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email con el que se intentó hacer login
    @Column(nullable = false, length = 150)
    private String email;

    // true = login exitoso, false = login fallido
    @Column(nullable = false)
    private Boolean exitoso;

    // IP desde donde se intentó el login
    // nullable porque no siempre está disponible
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // Fecha y hora exacta del intento
    @Column(name = "intento_at")
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
