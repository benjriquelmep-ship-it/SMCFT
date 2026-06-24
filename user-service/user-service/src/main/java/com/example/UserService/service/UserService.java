package com.example.UserService.service;

import com.example.UserService.dto.UserDTO;
import com.example.UserService.model.User;
import com.example.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {




    private final UserRepository userRepository;

    public List<User> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    public User obtenerPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con id {} no encontrado", id);
                    return new RuntimeException(
                            "Usuario no encontrado con id: " + id);
                });
    }

    public User obtenerPorEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario con email {} no encontrado", email);
                    return new RuntimeException(
                            "Usuario no encontrado con email: " + email);
                });
    }

    public User obtenerPorRut(String rut) {
        log.info("Buscando usuario con RUT: {}", rut);
        return userRepository.findByRut(rut)
                .orElseThrow(() -> {
                    log.warn("Usuario con RUT {} no encontrado", rut);
                    return new RuntimeException(
                            "Usuario no encontrado con RUT: " + rut);
                });
    }

    public User crear(UserDTO dto) {
        log.info("Creando usuario con RUT: {}", dto.getRut());

        // REGLA DE NEGOCIO 1: no puede existir otro usuario con el mismo RUT
        // Verificamos ANTES de guardar para evitar errores de BD
        if (userRepository.existsByRut(dto.getRut())) {
            log.warn("RUT duplicado: {}", dto.getRut());
            throw new RuntimeException(
                    "Ya existe un usuario con el RUT: " + dto.getRut());
        }

        // REGLA DE NEGOCIO 2: no puede existir otro usuario con el mismo email
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email duplicado: {}", dto.getEmail());
            throw new RuntimeException(
                    "Ya existe un usuario con el email: " + dto.getEmail());
        }

        User nuevo = new User();
        nuevo.setRut(dto.getRut());
        nuevo.setNombre(dto.getNombre());
        nuevo.setEmail(dto.getEmail());
        nuevo.setPassword(dto.getPassword());
        nuevo.setRol(dto.getRol());
        nuevo.setActivo(true);
        User guardado = userRepository.save(nuevo);
        log.info("Usuario creado con id: {}", guardado.getId());
        return guardado;
    }

    public User actualizar(Long id, UserDTO dto) {
        log.info("Actualizando usuario con id: {}", id);

        User existente = obtenerPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setRol(dto.getRol());

        User actualizado = userRepository.save(existente);
        log.info("Usuario {} actualizado correctamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        User existente = obtenerPorId(id);
        existente.setActivo(false);
        userRepository.save(existente);
        log.info("Usuario {} desactivado correctamente", id);
    }


    public List<User> obtenerActivos() {
        log.info("Obteniendo usuarios activos");
        return userRepository.findByActivoTrue();
    }

    public List<User> obtenerInactivos() {
        log.info("Obteniendo usuarios inactivos");
        return userRepository.findByActivoFalse();
    }

    public List<User> obtenerPorRol(String rol) {
        log.info("Obteniendo usuarios con rol: {}", rol);
        return userRepository.findByRol(rol);
    }

    public List<User> obtenerActivosPorRol(String rol) {
        log.info("Obteniendo usuarios activos con rol: {}", rol);
        return userRepository.findByRolAndActivoTrue(rol);
    }

    public List<User> buscarPorNombre(String texto) {
        log.info("Buscando usuarios que contienen en nombre: {}", texto);
        return userRepository.findByNombreContaining(texto);
    }

    public List<User> obtenerActivosOrdenados() {
        log.info("Obteniendo usuarios activos ordenados por nombre");
        return userRepository.findByActivoTrueOrderByNombreAsc();
    }

    public List<User> obtenerPrimerosCinco() {
        log.info("Obteniendo los primeros 5 usuarios");
        return userRepository.findTop5ByOrderByIdAsc();
    }
}