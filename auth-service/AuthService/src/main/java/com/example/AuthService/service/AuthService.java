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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // Para generar y validar tokens JWT
    // Es el ÚNICO microservicio que genera tokens — los demás solo los verifican
    private final JwtUtil jwtUtil;

    // Cliente HTTP para llamar a User Service
    // Se usa para verificar si el usuario existe y su contraseña
    private final WebClient webClient;

    // Accede a la tabla token_blacklist en la BD
    // Guarda los tokens que fueron invalidados por logout
    private final TokenBlacklistRepository tokenBlacklistRepository;

    // Accede a la tabla login_attempts en la BD
    // Registra cada intento de login — exitoso o fallido
    private final LoginAttemptRepository loginAttemptRepository;

    // Proceso completo de login en 4 pasos
    public TokenResponseDTO login(LoginDTO dto) {
        log.info("Intento de login para: {}", dto.getEmail());

        // PASO 1 — Consulta al User Service si existe el usuario
        // Si no existe → registra intento fallido y lanza error
        UserResponseDTO usuario =
                obtenerUsuarioDesdeUserService(dto.getEmail());

        // PASO 2 — REGLA: el usuario debe estar activo
        // Si activo = false → no puede iniciar sesión
        if (!usuario.getActivo()) {
            registrarIntento(dto.getEmail(), false);
            log.warn("Login fallido — usuario inactivo: {}", dto.getEmail());
            throw new RuntimeException("El usuario está inactivo");
        }

        // PASO 3 — REGLA: la contraseña debe coincidir
        // Compara la contraseña del formulario con la del User Service
        if (!usuario.getPassword().equals(dto.getPassword())) {
            registrarIntento(dto.getEmail(), false);
            log.warn("Login fallido — contraseña incorrecta: {}",
                    dto.getEmail());
            throw new RuntimeException("Credenciales incorrectas");
        }

        // PASO 4 — Genera el token JWT con email y rol del usuario
        // Este token se usa en todas las peticiones posteriores
        String token = jwtUtil.generarToken(
                usuario.getEmail(), usuario.getRol());

        // Registrar el intento exitoso en la bd
        registrarIntento(dto.getEmail(), true);

        log.info("Login exitoso para: {} con rol: {}",
                usuario.getEmail(), usuario.getRol());
        return new TokenResponseDTO(token, usuario.getRol(), "Login exitoso");
    }

    // Invalida el token guardándolo en la blacklist
    // Vincula el último intento exitoso con este token invalidado
    public void logout(String token) {
        log.info("Procesando logout");

        // REGLA: el token debe ser válido para poder invalidarlo
        // No tiene sentido invalidar un token que ya expiró
        if (!jwtUtil.esValido(token)) {
            log.warn("Se intentó invalidar un token ya inválido");
            throw new RuntimeException(
                    "El token ya es inválido o ha expirado");
        }

        // REGLA: no se duplican tokens en la blacklist
        // Si ya fue invalidado no se puede volver a invalidar
        if (tokenBlacklistRepository.existsByToken(token)) {
            log.warn("El token ya estaba en la blacklist");
            throw new RuntimeException("El token ya fue invalidado");
        }

        // Extrae el email del usuario desde el token
        String email = jwtUtil.obtenerEmail(token);

        // Guardar el token en la blacklist
        TokenBlacklist tokenInvalidado = new TokenBlacklist();
        tokenInvalidado.setToken(token); // el token invalidado
        tokenInvalidado.setEmail(email); // a quién pertenecía
        tokenInvalidado.setInvalidadoAt(LocalDateTime.now()); // cuándo se invalidó
        TokenBlacklist guardado =
                tokenBlacklistRepository.save(tokenInvalidado);

        // Busca el último intento exitoso del usuario
        // y lo vincula con este token invalidado
        // Esto establece la relación FK entre LoginAttempt y TokenBlacklist
        List<LoginAttempt> intentosExitosos =
                loginAttemptRepository.findByEmailAndExitosoTrue(email);
        if (!intentosExitosos.isEmpty()) {

            // Toma el último elemento de la lista (el más reciente)
            LoginAttempt ultimoIntento =
                    intentosExitosos.get(intentosExitosos.size() - 1);

            // Vincula el intento con el token invalidado
            ultimoIntento.setTokenBlacklist(guardado);
            loginAttemptRepository.save(ultimoIntento);
            log.info("Intento exitoso vinculado al token invalidado");
        }

        log.info("Logout exitoso — token invalidado para: {}", email);
    }

    // Verifica DOS condiciones:
    // 1. El token tiene firma válida y no ha expirado
    // 2. El token no fue invalidado por logout (blacklist)
    public boolean validarToken(String token) {
        log.info("Validando token");

        // Verificación 1 — firma y expiración
        if (!jwtUtil.esValido(token)) {
            log.warn("Token con firma inválida o expirado");
            return false;
        }

        // Verificación 2 — blacklist
        if (tokenBlacklistRepository.existsByToken(token)) {
            log.warn("Token encontrado en blacklist");
            return false;
        }

        log.info("Token válido");
        return true;
    }

    // Extrae el rol del token después de validarlo completamente
    // Si el token no es válido → lanza error
    public String obtenerRolDesdeToken(String token) {
        if (!validarToken(token)) {
            log.warn("Se intentó obtener rol de un token inválido");
            throw new RuntimeException("Token inválido o expirado");
        }
        return jwtUtil.obtenerRol(token);
    }

    // Devuelve todos los tokens invalidados de la BD
    public List<TokenBlacklist> obtenerTodaLaBlacklist() {
        log.info("Obteniendo todos los tokens invalidados");
        return tokenBlacklistRepository.findAll();
    }

    // Busca un token invalidado por su id
    // Si no existe → lanza RuntimeException → HTTP 401
    public TokenBlacklist obtenerBlacklistPorId(Long id) {
        log.info("Buscando registro de blacklist con id: {}", id);
        return tokenBlacklistRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Registro de blacklist {} no encontrado", id);
                    return new RuntimeException(
                            "Registro no encontrado con id: " + id);
                });
    }

    // Elimina un token de la blacklist por su id
    public void eliminarDeBlacklist(Long id) {
        log.info("Eliminando token de blacklist con id: {}", id);
        if (!tokenBlacklistRepository.existsById(id)) {
            throw new RuntimeException(
                    "Registro no encontrado con id: " + id);
        }
        tokenBlacklistRepository.deleteById(id);
        log.info("Registro {} eliminado correctamente", id);
    }

    // Devuelve todos los logouts de un usuario ordenados del más reciente
    public List<TokenBlacklist> obtenerHistorialLogout(String email) {
        log.info("Obteniendo historial de logout para: {}", email);
        return tokenBlacklistRepository
                .findByEmailOrderByInvalidadoAtDesc(email);
    }

    // Devuelve los últimos 10 tokens invalidados del sistema
    public List<TokenBlacklist> obtenerUltimosLogouts() {
        log.info("Obteniendo los últimos 10 tokens invalidados");
        return tokenBlacklistRepository.findTop10ByOrderByIdDesc();
    }

    // Devuelve todos los intentos de login de la BD
    public List<LoginAttempt> obtenerTodosLosIntentos() {
        log.info("Obteniendo todos los intentos de login");
        return loginAttemptRepository.findAll();
    }

    // Busca un intento de login por su id
    // Si no existe → lanza RuntimeException → HTTP 401
    public LoginAttempt obtenerIntentoPorId(Long id) {
        log.info("Buscando intento con id: {}", id);
        return loginAttemptRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento con id {} no encontrado", id);
                    return new RuntimeException(
                            "Intento no encontrado con id: " + id);
                });
    }

    // Elimina un intento de login por su id
    public void eliminarIntento(Long id) {
        log.info("Eliminando intento con id: {}", id);
        if (!loginAttemptRepository.existsById(id)) {
            throw new RuntimeException(
                    "Intento no encontrado con id: " + id);
        }
        loginAttemptRepository.deleteById(id);
        log.info("Intento {} eliminado correctamente", id);
    }

    // Devuelve todos los intentos de un usuario ordenados del más reciente
    public List<LoginAttempt> obtenerIntentosPorEmail(String email) {
        log.info("Obteniendo intentos para: {}", email);
        return loginAttemptRepository.findByEmailOrderByIntentoAtDesc(email);
    }

    // Devuelve solo los intentos FALLIDOS de un usuario
    public List<LoginAttempt> obtenerIntentosFallidos(String email) {
        log.info("Obteniendo intentos fallidos para: {}", email);
        return loginAttemptRepository.findByEmailAndExitosoFalse(email);
    }

    // Devuelve los últimos 10 intentos de login del sistema
    public List<LoginAttempt> obtenerUltimosIntentos() {
        log.info("Obteniendo los últimos 10 intentos");
        return loginAttemptRepository.findTop10ByOrderByIntentoAtDesc();
    }

    // Devuelve intentos de login en un rango de fechas
    public List<LoginAttempt> obtenerIntentosPorFechas(
            LocalDateTime desde, LocalDateTime hasta) {
        log.info("Obteniendo intentos entre {} y {}", desde, hasta);
        return loginAttemptRepository
                .findByIntentoAtBetween(desde, hasta);
    }

    // Registra un intento de login en la BD
    // Se llama en cada login exitoso o fallido
    private void registrarIntento(String email, boolean exitoso) {
        LoginAttempt intento = new LoginAttempt();
        intento.setEmail(email); // quién intentó
        intento.setExitoso(exitoso); // si fue exitoso o no
        intento.setIntentoAt(LocalDateTime.now());    // cuándo fue
        intento.setTokenBlacklist(null); // null hasta que haga logout
        loginAttemptRepository.save(intento);
        log.info("Intento {} registrado para: {}",
                exitoso ? "exitoso" : "fallido", email);
    }

    // Llama al User Service para obtener los datos del usuario
    // Si el usuario no existe → registra intento fallido y lanza error
    // Si User Service está caído → lanza error con mensaje claro
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

            // Cualquier otro error — User Service caído o sin conexión
            log.error("Error al comunicarse con User Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al conectar con el servicio de usuarios. "
                            + "Verifique que User Service esté corriendo en el puerto 8082");
        }
    }
}