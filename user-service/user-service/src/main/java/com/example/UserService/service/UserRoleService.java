package com.example.UserService.service;

import com.example.UserService.dto.UserRoleDTO;
import com.example.UserService.model.User;
import com.example.UserService.model.UserRole;
import com.example.UserService.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    private final UserService userService;

    public List<UserRole> obtenerTodos() {
        log.info("Obteniendo todos los roles");
        return userRoleRepository.findAll();
    }

    public UserRole obtenerPorId(Long id) {
        log.info("Buscando rol con id: {}", id);
        return userRoleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rol con id {} no encontrado", id);
                    return new RuntimeException(
                            "Rol no encontrado con id: " + id);
                });
    }

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

        UserRole nuevoRol = new UserRole();
        nuevoRol.setUser(usuario);
        nuevoRol.setRol(dto.getRol());
        nuevoRol.setAsignadoAt(LocalDateTime.now());
        nuevoRol.setActivo(true);

        UserRole guardado = userRoleRepository.save(nuevoRol);
        log.info("Rol {} asignado al usuario {}", dto.getRol(), dto.getUserId());
        return guardado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando rol con id: {}", id);
        if (!userRoleRepository.existsById(id)) {
            log.warn("Rol con id {} no encontrado", id);
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }
        userRoleRepository.deleteById(id);
        log.info("Rol {} eliminado correctamente", id);
    }


    public List<UserRole> obtenerPorUsuario(Long userId) {
        log.info("Obteniendo historial de roles del usuario: {}", userId);
        return userRoleRepository.findByUserIdOrderByAsignadoAtDesc(userId);
    }

    public List<UserRole> obtenerActivosPorUsuario(Long userId) {
        log.info("Obteniendo roles activos del usuario: {}", userId);
        return userRoleRepository.findByUserIdAndActivoTrue(userId);
    }

    public List<UserRole> obtenerPorRol(String rol) {
        log.info("Obteniendo roles de tipo: {}", rol);
        return userRoleRepository.findByRol(rol);
    }

    public List<UserRole> obtenerActivosPorRol(String rol) {
        log.info("Obteniendo roles activos de tipo: {}", rol);
        return userRoleRepository.findByRolAndActivoTrue(rol);
    }

    public List<UserRole> obtenerUltimosAsignados() {
        log.info("Obteniendo los últimos 10 roles asignados");
        return userRoleRepository.findTop10ByOrderByAsignadoAtDesc();
    }
}