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

    // Devuelve todos los intentos de login de un email específico
    // incluye exitosos y fallidos
    List<LoginAttempt> findByEmail(String email);

    // Solo intentos fallidos de un email
    List<LoginAttempt> findByEmailAndExitosoFalse(String email);

    // Solo intentos exitosos de un email
    List<LoginAttempt> findByEmailAndExitosoTrue(String email);

    // Intentos fallidos después de una fecha — para detectar fuerza bruta
    List<LoginAttempt> findByEmailAndExitosoFalseAndIntentoAtAfter(
            String email, LocalDateTime desde);

    // Contar intentos fallidos recientes — regla de negocio antifuerza bruta
    long countByEmailAndExitosoFalseAndIntentoAtAfter(
            String email, LocalDateTime desde);

    // Intentos en un rango de fechas — para reportes de auditoría
    List<LoginAttempt> findByIntentoAtBetween(
            LocalDateTime desde, LocalDateTime hasta);

    // Intentos de un email del más reciente al más antiguo
    List<LoginAttempt> findByEmailOrderByIntentoAtDesc(String email);

    // Los últimos 10 intentos del sistema
    List<LoginAttempt> findTop10ByOrderByIntentoAtDesc();

    // Intentos asociados a un token de blacklist específico
    List<LoginAttempt> findByTokenBlacklistId(Long tokenBlacklistId);
}