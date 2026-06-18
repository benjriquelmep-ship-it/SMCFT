package com.example.AuthService.service;

import com.example.AuthService.dto.LoginDTO;
import com.example.AuthService.dto.TokenResponseDTO;
import com.example.AuthService.dto.UserResponseDTO;
import com.example.AuthService.model.LoginAttempt;
import com.example.AuthService.model.TokenBlacklist;
import com.example.AuthService.repository.LoginAttemptRepository;
import com.example.AuthService.repository.TokenBlacklistRepository;
import com.example.AuthService.security.JwtUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private AuthService authService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;
    @Mock
    private LoginAttemptRepository loginAttemptRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void login_UsuarioActivoYPasswordCorrecta_RetornaToken() {
        // Probar que cuando usuario activo y password correcta → retorna token

        // Crear el DTO con los datos necesarios para la prueba
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("correcta");
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setEmail("test@test.com");
        userResponse.setPassword("correcta");
        userResponse.setRol("FISCALIZADOR");
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/email/{email}", "test@test.com")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));
        when(jwtUtil.generarToken("test@test.com", "FISCALIZADOR")).thenReturn("token123");

        // --- Ejecutar el método del servicio que se está probando ---
        TokenResponseDTO result = authService.login(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals("token123", result.getToken());
        assertEquals("FISCALIZADOR", result.getRol());
        assertEquals("Login exitoso", result.getMensaje());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
    @Test

    void login_UsuarioInactivo_LanzaExcepcion() {
        // Probar que cuando usuario inactivo → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("correcta");
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setEmail("test@test.com");
        userResponse.setActivo(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/email/{email}", "test@test.com")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        // Llamar al servicio y capturar la excepción lanzada

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("El usuario está inactivo", ex.getMessage());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
    @Test

    void login_PasswordIncorrecta_LanzaExcepcion() {
        // Probar que cuando password incorrecta → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("incorrecta");
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setEmail("test@test.com");
        userResponse.setPassword("correcta");
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/email/{email}", "test@test.com")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        // Llamar al servicio y capturar la excepción lanzada

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("Credenciales incorrectas", ex.getMessage());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(loginAttemptRepository, times(1)).save(any(LoginAttempt.class));
    }
    @Test

    void login_UsuarioNoEncontrado_LanzaExcepcion() {
        // Probar que cuando usuario no encontrado → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        LoginDTO dto = new LoginDTO();
        dto.setEmail("noexiste@test.com");
        dto.setPassword("correcta");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/email/{email}", "noexiste@test.com")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenThrow(
            WebClientResponseException.create(404, "Not Found", null, null, null));

        // Llamar al servicio y capturar la excepción lanzada

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }
    @Test

    void logout_TokenValido_GuardaEnBlacklist() {
        // Probar que cuando token valido → guarda en blacklist

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token123")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("token123")).thenReturn(false);
        when(jwtUtil.obtenerEmail("token123")).thenReturn("test@test.com");
        when(tokenBlacklistRepository.save(any(TokenBlacklist.class))).thenAnswer(i -> i.getArgument(0));
        when(loginAttemptRepository.findByEmailAndExitosoTrue("test@test.com")).thenReturn(List.of());

        // --- Ejecutar el método del servicio que se está probando ---
        authService.logout("token123");

        // --- Verificar que el resultado sea el correcto ---
        verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklist.class));
    }
    @Test

    void logout_TokenInvalido_LanzaExcepcion() {
        // Probar que cuando token invalido → lanza excepcion

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_invalido")).thenReturn(false);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.logout("token_invalido"));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("El token ya es inválido o ha expirado", ex.getMessage());
    }
    @Test

    void logout_TokenYaEnBlacklist_LanzaExcepcion() {
        // Probar que cuando token ya en blacklist → lanza excepcion

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token123")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("token123")).thenReturn(true);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.logout("token123"));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("El token ya fue invalidado", ex.getMessage());
    }
    @Test

    void validarToken_FirmaValidaYNoEnBlacklist_RetornaTrue() {
        // Probar que cuando firma valida y no en blacklist → retorna true

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_valido")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("token_valido")).thenReturn(false);

        // --- Ejecutar el método del servicio que se está probando ---
        boolean result = authService.validarToken("token_valido");

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(result);
    }
    @Test

    void validarToken_FirmaInvalida_RetornaFalse() {
        // Probar que cuando firma invalida → retorna false

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_invalido")).thenReturn(false);

        // --- Ejecutar el método del servicio que se está probando ---
        boolean result = authService.validarToken("token_invalido");

        // --- Verificar que el resultado sea el correcto ---
        assertFalse(result);

        // Verificar que el repositorio NO haya sido invocado
        verify(tokenBlacklistRepository, never()).existsByToken(any());
    }
    @Test

    void validarToken_EnBlacklist_RetornaFalse() {
        // Probar que cuando en blacklist → retorna false

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_valido")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("token_valido")).thenReturn(true);

        // --- Ejecutar el método del servicio que se está probando ---
        boolean result = authService.validarToken("token_valido");

        // --- Verificar que el resultado sea el correcto ---
        assertFalse(result);
    }
    @Test

    void obtenerRolDesdeToken_TokenValido_RetornaRol() {
        // Probar que cuando token valido → retorna rol

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_valido")).thenReturn(true);
        when(tokenBlacklistRepository.existsByToken("token_valido")).thenReturn(false);
        when(jwtUtil.obtenerRol("token_valido")).thenReturn("FISCALIZADOR");

        // --- Ejecutar el método del servicio que se está probando ---
        String rol = authService.obtenerRolDesdeToken("token_valido");

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("FISCALIZADOR", rol);
    }
    @Test

    void obtenerRolDesdeToken_TokenInvalido_LanzaExcepcion() {
        // Probar que cuando token invalido → lanza excepcion

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(jwtUtil.esValido("token_invalido")).thenReturn(false);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.obtenerRolDesdeToken("token_invalido"));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("Token inválido o expirado", ex.getMessage());
    }
}