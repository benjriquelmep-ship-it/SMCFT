// Repositorio JPA para gestionar las consultas y operaciones sobre la tabla "user_roles"
package com.example.UserService.repository;

// Acceso a datos de la tabla user_roles
// Consultas derivadas del apunte integradas

import com.example.UserService.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
    // IGUALDAD BÁSICA
    // Todos los roles de un usuario por su id
    // Spring genera: SELECT * FROM user_roles WHERE user_id = ?
    List<UserRole> findByUserId(Long userId);

    // AND
    // Solo roles activos de un usuario
    // Spring genera: SELECT * FROM user_roles WHERE user_id = ? AND activo = true
    List<UserRole> findByUserIdAndActivoTrue(Long userId);

    // IGUALDAD CON ROL
    // Spring genera: SELECT * FROM user_roles WHERE rol = ?
    List<UserRole> findByRol(String rol);

    // AND CON ROL Y ACTIVO
    // Spring genera: SELECT * FROM user_roles WHERE rol = ? AND activo = true
    List<UserRole> findByRolAndActivoTrue(String rol);

    // BOOLEANOS
    // Spring genera: SELECT * FROM user_roles WHERE activo = true
    List<UserRole> findByActivoTrue();
    List<UserRole> findByActivoFalse();

    // ORDENAMIENTO
    // Historial de roles de un usuario del más reciente al más antiguo
    // Spring genera: SELECT * FROM user_roles WHERE user_id = ?
    //                ORDER BY asignado_at DESC
    List<UserRole> findByUserIdOrderByAsignadoAtDesc(Long userId);

    // TOP
    // Los últimos 10 roles asignados en el sistema
    // Spring genera: SELECT * FROM user_roles ORDER BY asignado_at DESC LIMIT 10
    List<UserRole> findTop10ByOrderByAsignadoAtDesc();
}