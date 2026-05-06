package com.example.UserService.service;

import com.example.UserService.dto.UserRoleDTO;
import com.example.UserService.model.User;
import com.example.UserService.model.UserRole;
import com.example.UserService.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class UserRoleService {
    private static final Logger log =
            LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    // Inyectamos UserService para verificar que el usuario existe
    private final UserService userService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Obtener todos los roles del sistema
    public List<UserRole> obtenerTodos() {
        log.info("Obteniendo todos los roles");
        return userRoleRepository.findAll();
    }

    // Obtener rol por id
    public UserRole obtenerPorId(Long id) {
        log.info("Buscando rol con id: {}", id);
        return userRoleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rol con id {} no encontrado", id);
                    return new RuntimeException(
                            "Rol no encontrado con id: " + id);
                });
    }

    // Asignar un nuevo rol a un usuario
    // Desactiva el rol actual y crea uno nuevo
    public UserRole asignar(UserRoleDTO dto) {
        log.info("Asignando rol {} al usuario id: {}",
                dto.getRol(), dto.getUserId());

        // REGLA DE NEGOCIO: el usuario debe existir
        User usuario = userService.obtenerPorId(dto.getUserId());

        // REGLA DE NEGOCIO: desactivar el rol actual antes de asignar uno nuevo
        // Un usuario solo puede tener un rol activo a la vez
        List<UserRole> rolesActivos = userRoleRepository
                .findByUserIdAndActivoTrue(dto.getUserId());
        rolesActivos.forEach(r -> {
            r.setActivo(false);
            userRoleRepository.save(r);
            log.info("Rol anterior {} desactivado para usuario {}",
                    r.getRol(), dto.getUserId());
        });

        // Crear el nuevo rol histórico
        UserRole nuevoRol = new UserRole();
        nuevoRol.setUser(usuario);
        nuevoRol.setRol(dto.getRol());
        nuevoRol.setAsignadoAt(LocalDateTime.now());
        nuevoRol.setActivo(true);

        UserRole guardado = userRoleRepository.save(nuevoRol);
        log.info("Rol {} asignado al usuario {}", dto.getRol(), dto.getUserId());
        return guardado;
    }

    // Eliminar un rol del historial
    public void eliminar(Long id) {
        log.info("Eliminando rol con id: {}", id);
        if (!userRoleRepository.existsById(id)) {
            log.warn("Rol con id {} no encontrado", id);
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }
        userRoleRepository.deleteById(id);
        log.info("Rol {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // Historial completo de roles de un usuario
    // Ordenado del más reciente al más antiguo
    public List<UserRole> obtenerPorUsuario(Long userId) {
        log.info("Obteniendo historial de roles del usuario: {}", userId);
        return userRoleRepository.findByUserIdOrderByAsignadoAtDesc(userId);
    }

    // Solo el rol activo actual de un usuario
    public List<UserRole> obtenerActivosPorUsuario(Long userId) {
        log.info("Obteniendo roles activos del usuario: {}", userId);
        return userRoleRepository.findByUserIdAndActivoTrue(userId);
    }

    // Roles por tipo específico
    // Ej: todos los roles de tipo FISCALIZADOR en el historial
    public List<UserRole> obtenerPorRol(String rol) {
        log.info("Obteniendo roles de tipo: {}", rol);
        return userRoleRepository.findByRol(rol);
    }

    // Roles activos por tipo
    public List<UserRole> obtenerActivosPorRol(String rol) {
        log.info("Obteniendo roles activos de tipo: {}", rol);
        return userRoleRepository.findByRolAndActivoTrue(rol);
    }

    // Los últimos 10 roles asignados en todo el sistema
    // Útil para auditoría y monitoreo
    public List<UserRole> obtenerUltimosAsignados() {
        log.info("Obteniendo los últimos 10 roles asignados");
        return userRoleRepository.findTop10ByOrderByAsignadoAtDesc();
    }
}

