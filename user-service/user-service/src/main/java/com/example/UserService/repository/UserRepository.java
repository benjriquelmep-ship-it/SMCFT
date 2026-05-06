package com.example.UserService.repository;

// JpaRepository ya te da save(), findById(), findAll(), delete() gratis

import com.example.UserService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // IGUALDAD BÁSICA
    // Spring genera: SELECT * FROM users WHERE rut = ?
    // Auth Service y Vehicle Service usan estos métodos
    Optional<User> findByRut(String rut);

    // Spring genera: SELECT * FROM users WHERE email = ?
    // Auth Service llama GET /api/v1/users/email/{email} durante el login
    Optional<User> findByEmail(String email);

    // EXISTENCIA
    // Spring genera: SELECT COUNT(*) > 0 FROM users WHERE rut = ?
    // Se usa para validar duplicados antes de guardar
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);

    // BOOLEANOS
    // Spring genera: SELECT * FROM users WHERE activo = true
    List<User> findByActivoTrue();

    // Spring genera: SELECT * FROM users WHERE activo = false
    // Útil para ver usuarios desactivados (soft delete)
    List<User> findByActivoFalse();

    // ROL
    // Spring genera: SELECT * FROM users WHERE rol = ?
    // Ej: findByRol("FISCALIZADOR") → todos los fiscalizadores
    List<User> findByRol(String rol);

    // AND
    // Spring genera: SELECT * FROM users WHERE rol = ? AND activo = true
    // Ej: todos los fiscalizadores que pueden operar ahora mismo
    List<User> findByRolAndActivoTrue(String rol);

    // OR
    // Spring genera: SELECT * FROM users WHERE rut = ? OR email = ?
    Optional<User> findByRutOrEmail(String rut, String email);

    // CONTAINING
    // Spring genera: SELECT * FROM users WHERE nombre LIKE '%texto%'
    // Útil para buscadores donde el usuario escribe parte del nombre
    List<User> findByNombreContaining(String texto);

    // IGNORE CASE
    // Spring genera: SELECT * FROM users WHERE LOWER(nombre) = LOWER(?)
    List<User> findByNombreIgnoreCase(String nombre);

    // ORDENAMIENTO
    // Spring genera: SELECT * FROM users WHERE activo = true ORDER BY nombre ASC
    List<User> findByActivoTrueOrderByNombreAsc();

    // TOP
    // Spring genera: SELECT * FROM users ORDER BY id ASC LIMIT 5
    List<User> findTop5ByOrderByIdAsc();
}