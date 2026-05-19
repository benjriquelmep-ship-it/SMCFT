package com.example.UserService.service;

// Aquí va TODA la lógica de negocio, nunca en el Controller

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
@RequiredArgsConstructor  // Lombok genera el constructor con los campos final (inyección de dependencias)
@Slf4j
public class UserService {
    // Logger SLF4J — obligatorio para la evaluación (IE 2.3.2)
    // getLogger asocia el logger a esta clase


    // Inyección por constructor — forma recomendada en Spring Boot moderno
    private final UserRepository userRepository;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Retorna todos los usuarios sin filtro
    public List<User> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");
        // findAll() viene gratis de JpaRepository
        return userRepository.findAll();
    }

    // Busca un usuario por su id
    // orElseThrow lanza RuntimeException si no existe
    // GlobalExceptionHandler la atrapa y retorna HTTP 404
    public User obtenerPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario con id {} no encontrado", id);
                    return new RuntimeException(
                            "Usuario no encontrado con id: " + id);
                });
    }

    // Auth Service llama a GET /api/v1/users/email/{email}
    // que llega aquí para verificar credenciales durante el login
    public User obtenerPorEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario con email {} no encontrado", email);
                    return new RuntimeException(
                            "Usuario no encontrado con email: " + email);
                });
    }

    // Vehicle Service llama a GET /api/v1/users/rut/{rut}
    // para verificar que el propietario existe antes de registrar un vehículo
    public User obtenerPorRut(String rut) {
        log.info("Buscando usuario con RUT: {}", rut);
        return userRepository.findByRut(rut)
                .orElseThrow(() -> {
                    log.warn("Usuario con RUT {} no encontrado", rut);
                    return new RuntimeException(
                            "Usuario no encontrado con RUT: " + rut);
                });
    }

    // Crea un nuevo usuario en el sistema
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

        // Mapeo DTO → Entidad
        // No guardamos el DTO directamente — primero lo convertimos a entidad
        User nuevo = new User();
        nuevo.setRut(dto.getRut());
        nuevo.setNombre(dto.getNombre());
        nuevo.setEmail(dto.getEmail());
        nuevo.setPassword(dto.getPassword());
        nuevo.setRol(dto.getRol());
        // Siempre creamos usuarios activos por defecto
        nuevo.setActivo(true);

        // save() ejecuta INSERT en MySQL y retorna el objeto con el id generado
        User guardado = userRepository.save(nuevo);
        log.info("Usuario creado con id: {}", guardado.getId());
        return guardado;
    }

    // Actualiza los datos de un usuario existente
    public User actualizar(Long id, UserDTO dto) {
        log.info("Actualizando usuario con id: {}", id);

        // Reutilizamos obtenerPorId que ya lanza error si no existe
        User existente = obtenerPorId(id);

        // No actualizamos el RUT — es el identificador único del ciudadano
        // No actualizamos la password aquí — eso sería un endpoint separado
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setRol(dto.getRol());

        // save() con un objeto que ya tiene id ejecuta UPDATE en MySQL
        User actualizado = userRepository.save(existente);
        log.info("Usuario {} actualizado correctamente", id);
        return actualizado;
    }

    // Soft delete — no borramos el registro de la BD
    // Solo marcamos activo = false para conservar el historial
    // Importante en sistemas de auditoría como el fronterizo
    public void eliminar(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        User existente = obtenerPorId(id);
        existente.setActivo(false);
        // save() ejecuta UPDATE con activo = false, NO DELETE
        userRepository.save(existente);
        log.info("Usuario {} desactivado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    // Usuarios con activo = true
    public List<User> obtenerActivos() {
        log.info("Obteniendo usuarios activos");
        return userRepository.findByActivoTrue();
    }

    // Usuarios con activo = false (desactivados)
    public List<User> obtenerInactivos() {
        log.info("Obteniendo usuarios inactivos");
        return userRepository.findByActivoFalse();
    }

    // Usuarios por rol específico
    // Ej: obtenerPorRol("FISCALIZADOR") → todos los fiscalizadores
    public List<User> obtenerPorRol(String rol) {
        log.info("Obteniendo usuarios con rol: {}", rol);
        return userRepository.findByRol(rol);
    }

    // Usuarios activos filtrados por rol
    // Ej: fiscalizadores que pueden operar en el sistema ahora mismo
    public List<User> obtenerActivosPorRol(String rol) {
        log.info("Obteniendo usuarios activos con rol: {}", rol);
        return userRepository.findByRolAndActivoTrue(rol);
    }

    // Búsqueda parcial por nombre
    // Ej: buscarPorNombre("juan") encuentra "Juan Pérez" y "Ana Juanita"
    public List<User> buscarPorNombre(String texto) {
        log.info("Buscando usuarios que contienen en nombre: {}", texto);
        return userRepository.findByNombreContaining(texto);
    }

    // Usuarios activos ordenados alfabéticamente A → Z
    public List<User> obtenerActivosOrdenados() {
        log.info("Obteniendo usuarios activos ordenados por nombre");
        return userRepository.findByActivoTrueOrderByNombreAsc();
    }

    // Los primeros 5 usuarios registrados en el sistema
    public List<User> obtenerPrimerosCinco() {
        log.info("Obteniendo los primeros 5 usuarios");
        return userRepository.findTop5ByOrderByIdAsc();
    }
}


