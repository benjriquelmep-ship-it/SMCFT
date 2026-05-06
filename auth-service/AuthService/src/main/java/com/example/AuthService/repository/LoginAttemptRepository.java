// repository/LoginAttemptRepository.java
// Acceso a datos de la tabla login_attempts
// Consultas derivadas del apunte integradas

package com.example.AuthService.repository;

import com.example.AuthService.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Long> {

    // IGUALDAD BÁSICA
    // Todos los intentos de un email
    // Spring genera: SELECT * FROM login_attempts WHERE email = ?
    List<LoginAttempt> findByEmail(String email);

    // BOOLEANOS
    // Solo intentos fallidos de un email
    // Spring genera: SELECT * FROM login_attempts WHERE email = ? AND exitoso = false
    List<LoginAttempt> findByEmailAndExitosoFalse(String email);

    // Solo intentos exitosos de un email
    // Spring genera: SELECT * FROM login_attempts WHERE email = ? AND exitoso = true
    List<LoginAttempt> findByEmailAndExitosoTrue(String email);

    // AND CON FECHA
    // Intentos fallidos después de una fecha — para detectar fuerza bruta
    // Spring genera: SELECT * FROM login_attempts
    //                WHERE email = ? AND exitoso = false AND intento_at > ?
    List<LoginAttempt> findByEmailAndExitosoFalseAndIntentoAtAfter(
            String email, LocalDateTime desde);

    // COUNT
    // Contar intentos fallidos recientes — regla de negocio antifuerza bruta
    // Spring genera: SELECT COUNT(*) FROM login_attempts
    //                WHERE email = ? AND exitoso = false AND intento_at > ?
    long countByEmailAndExitosoFalseAndIntentoAtAfter(
            String email, LocalDateTime desde);

    // BETWEEN
    // Intentos en un rango de fechas — para reportes de auditoría
    // Spring genera: SELECT * FROM login_attempts
    //                WHERE intento_at BETWEEN ? AND ?
    List<LoginAttempt> findByIntentoAtBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // ORDENAMIENTO
    // Intentos de un email del más reciente al más antiguo
    // Spring genera: SELECT * FROM login_attempts
    //                WHERE email = ? ORDER BY intento_at DESC
    List<LoginAttempt> findByEmailOrderByIntentoAtDesc(String email);

    // TOP
    // Los últimos 10 intentos del sistema
    // Spring genera: SELECT * FROM login_attempts ORDER BY intento_at DESC LIMIT 10
    List<LoginAttempt> findTop10ByOrderByIntentoAtDesc();

    // Intentos asociados a un token de blacklist específico
    // Spring genera: SELECT * FROM login_attempts WHERE token_blacklist_id = ?
    List<LoginAttempt> findByTokenBlacklistId(Long tokenBlacklistId);
}