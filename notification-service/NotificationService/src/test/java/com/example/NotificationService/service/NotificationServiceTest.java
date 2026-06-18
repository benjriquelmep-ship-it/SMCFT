package com.example.NotificationService.service;

import com.example.NotificationService.dto.NotificationDTO;
import com.example.NotificationService.model.Notification;
import com.example.NotificationService.repository.NotificationRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private NotificationService notificationService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient userServiceWebClient;
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

    // Se ejecuta antes de cada test para preparar el entorno
    @BeforeEach

    void setUp() {
        notificationService = new NotificationService(
                notificationRepository, webClient, userServiceWebClient);
    }
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void crear_DatosValidos_GuardaNotificacion() {
        // Probar que cuando datos validos → guarda notificacion

        // Crear el DTO con los datos necesarios para la prueba
        NotificationDTO dto = new NotificationDTO();
        dto.setTitulo("Notificación de prueba");
        dto.setMensaje("Notificación de prueba");
        dto.setTipo("ALERTA");
        dto.setDestinatarioRut("12345678-9");

        Map<String, Object> userData = Map.of("email", "test@test.com", "nombre", "Test User");
        when(userServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/rut/{rut}", "12345678-9")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(userData));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(1L);
            return n;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Notification result = notificationService.crear(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}