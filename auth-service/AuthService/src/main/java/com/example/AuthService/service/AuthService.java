// service/AuthService.java
// Lógica de negocio completa del Auth Service
// Se comunica con User Service para verificar credenciales
// Maneja la blacklist de tokens y el registro de intentos

package com.example.AuthService.service;

import com.example.AuthService.dto.LoginDTO;
import com.example.AuthService.dto.TokenResponseDTO;
import com.example.AuthService.dto.UserResponseDTO;
import com.example.AuthService.model.LoginAttempt;
import com.example.AuthService.model.TokenBlacklist;
import com.example.AuthService.repository.LoginAttemptRepository;
import com.example.AuthService.repository.TokenBlacklistRepository;
import com.example.AuthService.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    // Para generar y validar tokens JWT
    private final JwtUtil jwtUtil;

    // Para hacer peticiones HTTP a User Service
    private final WebClient webClient;

    // Para guardar y consultar tokens invalidados
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // Para registrar intentos de login
    private final LoginAttemptRepository loginAttemptRepository;

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------

    // Proceso completo de login en 4 pasos
    public TokenResponseDTO login(LoginDTO dto) {
        log.info("Intento de login para: {}", dto.getEmail());

        // PASO 1 — Consultar a User Service si existe el usuario
        // Comunicación entre microservicios — IE 2.4.1 de la rúbrica
        UserResponseDTO usuario =
                obtenerUsuarioDesdeUserService(dto.getEmail());

        // PASO 2 — Regla de negocio: usuario debe estar activo
        if (!usuario.getActivo()) {
            registrarIntento(dto.getEmail(), false);
            log.warn("Login fallido — usuario inactivo: {}", dto.getEmail());
            throw new RuntimeException("El usuario está inactivo");
        }

        // PASO 3 — Regla de negocio: contraseña debe coincidir
        if (!usuario.getPassword().equals(dto.getPassword())) {
            registrarIntento(dto.getEmail(), false);
            log.warn("Login fallido — contraseña incorrecta: {}",
                    dto.getEmail());
            throw new RuntimeException("Credenciales incorrectas");
        }

        // PASO 4 — Generar el token JWT con email y rol
        String token = jwtUtil.generarToken(
                usuario.getEmail(), usuario.getRol());

        // Registrar el intento exitoso
        registrarIntento(dto.getEmail(), true);

        log.info("Login exitoso para: {} con rol: {}",
                usuario.getEmail(), usuario.getRol());
        return new TokenResponseDTO(token, usuario.getRol(), "Login exitoso");
    }

    // -------------------------------------------------------
    // LOGOUT
    // -------------------------------------------------------

    // Invalida el token guardándolo en la blacklist
    // Vincula el último intento exitoso con este token invalidado
    public void logout(String token) {
        log.info("Procesando logout");

        // Regla de negocio: el token debe ser válido para invalidarlo
        if (!jwtUtil.esValido(token)) {
            log.warn("Se intentó invalidar un token ya inválido");
            throw new RuntimeException(
                    "El token ya es inválido o ha expirado");
        }

        // Regla de negocio: no duplicar tokens en la blacklist
        if (tokenBlacklistRepository.existsByToken(token)) {
            log.warn("El token ya estaba en la blacklist");
            throw new RuntimeException("El token ya fue invalidado");
        }

        String email = jwtUtil.obtenerEmail(token);

        // Guardar el token en la blacklist
        TokenBlacklist tokenInvalidado = new TokenBlacklist();
        tokenInvalidado.setToken(token);
        tokenInvalidado.setEmail(email);
        tokenInvalidado.setInvalidadoAt(LocalDateTime.now());
        TokenBlacklist guardado =
                tokenBlacklistRepository.save(tokenInvalidado);

        // Vincular el último intento exitoso con este token
        // Esto establece la relación entre LoginAttempt y TokenBlacklist
        List<LoginAttempt> intentosExitosos =
                loginAttemptRepository.findByEmailAndExitosoTrue(email);
        if (!intentosExitosos.isEmpty()) {
            LoginAttempt ultimoIntento =
                    intentosExitosos.get(intentosExitosos.size() - 1);
            ultimoIntento.setTokenBlacklist(guardado);
            loginAttemptRepository.save(ultimoIntento);
            log.info("Intento exitoso vinculado al token invalidado");
        }

        log.info("Logout exitoso — token invalidado para: {}", email);
    }

    // -------------------------------------------------------
    // VALIDACIÓN DE TOKEN
    // -------------------------------------------------------

    // Verifica DOS condiciones:
    // 1. El token tiene firma válida y no ha expirado
    // 2. El token no fue invalidado por logout (blacklist)
    public boolean validarToken(String token) {
        log.info("Validando token");

        if (!jwtUtil.esValido(token)) {
            log.warn("Token con firma inválida o expirado");
            return false;
        }

        if (tokenBlacklistRepository.existsByToken(token)) {
            log.warn("Token encontrado en blacklist");
            return false;
        }

        log.info("Token válido");
        return true;
    }

    // Extrae el rol del token después de validarlo completamente
    public String obtenerRolDesdeToken(String token) {
        if (!validarToken(token)) {
            log.warn("Se intentó obtener rol de un token inválido");
            throw new RuntimeException("Token inválido o expirado");
        }
        return jwtUtil.obtenerRol(token);
    }

    // -------------------------------------------------------
    // CRUD BLACKLIST
    // -------------------------------------------------------

    public List<TokenBlacklist> obtenerTodaLaBlacklist() {
        log.info("Obteniendo todos los tokens invalidados");
        return tokenBlacklistRepository.findAll();
    }

    public TokenBlacklist obtenerBlacklistPorId(Long id) {
        log.info("Buscando registro de blacklist con id: {}", id);
        return tokenBlacklistRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Registro de blacklist {} no encontrado", id);
                    return new RuntimeException(
                            "Registro no encontrado con id: " + id);
                });
    }

    public void eliminarDeBlacklist(Long id) {
        log.info("Eliminando token de blacklist con id: {}", id);
        if (!tokenBlacklistRepository.existsById(id)) {
            throw new RuntimeException(
                    "Registro no encontrado con id: " + id);
        }
        tokenBlacklistRepository.deleteById(id);
        log.info("Registro {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS — BLACKLIST
    // -------------------------------------------------------

    public List<TokenBlacklist> obtenerHistorialLogout(String email) {
        log.info("Obteniendo historial de logout para: {}", email);
        return tokenBlacklistRepository
                .findByEmailOrderByInvalidadoAtDesc(email);
    }

    public List<TokenBlacklist> obtenerUltimosLogouts() {
        log.info("Obteniendo los últimos 10 tokens invalidados");
        return tokenBlacklistRepository.findTop10ByOrderByIdDesc();
    }

    // -------------------------------------------------------
    // CRUD LOGIN ATTEMPTS
    // -------------------------------------------------------

    public List<LoginAttempt> obtenerTodosLosIntentos() {
        log.info("Obteniendo todos los intentos de login");
        return loginAttemptRepository.findAll();
    }

    public LoginAttempt obtenerIntentoPorId(Long id) {
        log.info("Buscando intento con id: {}", id);
        return loginAttemptRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento con id {} no encontrado", id);
                    return new RuntimeException(
                            "Intento no encontrado con id: " + id);
                });
    }

    public void eliminarIntento(Long id) {
        log.info("Eliminando intento con id: {}", id);
        if (!loginAttemptRepository.existsById(id)) {
            throw new RuntimeException(
                    "Intento no encontrado con id: " + id);
        }
        loginAttemptRepository.deleteById(id);
        log.info("Intento {} eliminado correctamente", id);
    }

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS — LOGIN ATTEMPTS
    // -------------------------------------------------------

    public List<LoginAttempt> obtenerIntentosPorEmail(String email) {
        log.info("Obteniendo intentos para: {}", email);
        return loginAttemptRepository.findByEmailOrderByIntentoAtDesc(email);
    }

    public List<LoginAttempt> obtenerIntentosFallidos(String email) {
        log.info("Obteniendo intentos fallidos para: {}", email);
        return loginAttemptRepository.findByEmailAndExitosoFalse(email);
    }

    public List<LoginAttempt> obtenerUltimosIntentos() {
        log.info("Obteniendo los últimos 10 intentos");
        return loginAttemptRepository.findTop10ByOrderByIntentoAtDesc();
    }

    public List<LoginAttempt> obtenerIntentosPorFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo intentos entre {} y {}", desde, hasta);
        return loginAttemptRepository
                .findByIntentoAtBetween(desde, hasta);
    }

    // -------------------------------------------------------
    // MÉTODOS PRIVADOS
    // -------------------------------------------------------

    // Registra un intento de login en la BD
    // Se llama en cada login exitoso o fallido
    private void registrarIntento(String email, boolean exitoso) {
        LoginAttempt intento = new LoginAttempt();
        intento.setEmail(email);
        intento.setExitoso(exitoso);
        intento.setIntentoAt(LocalDateTime.now());
        // tokenBlacklist = null porque aún no se ha hecho logout
        intento.setTokenBlacklist(null);
        loginAttemptRepository.save(intento);
        log.info("Intento {} registrado para: {}",
                exitoso ? "exitoso" : "fallido", email);
    }

    // Consulta a User Service si existe el usuario con ese email
    private UserResponseDTO obtenerUsuarioDesdeUserService(String email) {
        try {
            log.info("Consultando User Service para: {}", email);

            return webClient.get()
                    // GET http://localhost:8082/api/v1/users/email/juan@gmail.com
                    .uri("/api/v1/users/email/{email}", email)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            // User Service retornó 404 — el usuario no existe
            // Decimos "Credenciales incorrectas" por seguridad
            registrarIntento(email, false);
            log.warn("Usuario no encontrado en User Service: {}", email);
            throw new RuntimeException("Credenciales incorrectas");

        } catch (Exception e) {
            // User Service caído, timeout, error de red, etc.
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al conectar con el servicio de usuarios. "
                            + "Verifique que User Service esté corriendo en el puerto 8082");
        }
    }
}