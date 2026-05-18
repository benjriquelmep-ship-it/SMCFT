// Acceso a datos de la tabla token_blacklist
package com.example.AuthService.repository;

import com.example.AuthService.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    // Verifica si un token fue invalidado — usado en JwtFilter y validarToken()
    boolean existsByToken(String token);

    // Todos los tokens invalidados de un usuario
    List<TokenBlacklist> findByEmail(String email);

    // Buscar por email parcial — útil para buscadores de auditoría
    List<TokenBlacklist> findByEmailContaining(String texto);

    // Historial del más reciente al más antiguo
    List<TokenBlacklist> findByEmailOrderByInvalidadoAtDesc(String email);

    // Los últimos 10 tokens invalidados en el sistema
    List<TokenBlacklist> findTop10ByOrderByIdDesc();
}