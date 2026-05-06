// repository/TokenBlacklistRepository.java
// Acceso a datos de la tabla token_blacklist
// Consultas derivadas del apunte integradas

package com.example.AuthService.repository;

import com.example.AuthService.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    // EXISTENCIA
    // Verifica si un token fue invalidado — usado en JwtFilter y validarToken()
    // Spring genera: SELECT COUNT(*) > 0 FROM token_blacklist WHERE token = ?
    boolean existsByToken(String token);

    // IGUALDAD BÁSICA
    // Todos los tokens invalidados de un usuario
    // Spring genera: SELECT * FROM token_blacklist WHERE email = ?
    List<TokenBlacklist> findByEmail(String email);

    // CONTAINING
    // Buscar por email parcial — útil para buscadores de auditoría
    // Spring genera: SELECT * FROM token_blacklist WHERE email LIKE '%texto%'
    List<TokenBlacklist> findByEmailContaining(String texto);

    // ORDENAMIENTO
    // Historial del más reciente al más antiguo
    // Spring genera: SELECT * FROM token_blacklist WHERE email = ?
    //                ORDER BY invalidado_at DESC
    List<TokenBlacklist> findByEmailOrderByInvalidadoAtDesc(String email);

    // TOP
    // Los últimos 10 tokens invalidados en el sistema
    // Spring genera: SELECT * FROM token_blacklist ORDER BY id DESC LIMIT 10
    List<TokenBlacklist> findTop10ByOrderByIdDesc();
}