package com.example.AuditService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.example.AuditService.dto.AuditDTO;
import com.example.AuditService.dto.UserResponseDTO;
import com.example.AuditService.model.Audit;
import com.example.AuditService.repository.AuditRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private AuditService auditService;

    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private AuditRepository auditRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void registrar_DatosValidos_RegistraAuditoria() {
        // Probar que cuando datos validos → registra auditoria

        // Crear el DTO con los datos necesarios para la prueba
        AuditDTO dto = new AuditDTO();
        dto.setRutAuditor("9876543-2");
        dto.setTipoAuditoria("CRUCE_FRONTERIZO");
        dto.setEntidad("User");
        dto.setEntidadId(1L);
        dto.setDescripcion("Revisión ordinaria de usuarios");
        dto.setFechaInicio(LocalDateTime.now().minusHours(2));

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        when(auditRepository.save(any(Audit.class))).thenAnswer(i -> {
            Audit a = i.getArgument(0);
            a.setId(1L);
            return a;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Audit result = auditService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EN_PROCESO", result.getEstado());
    }

    @Test

    void completar_EnProceso_CambiaEstado() {
        // Probar que cuando en proceso → cambia estado

        Audit mockAudit = new Audit();
        mockAudit.setId(1L);
        mockAudit.setEstado("EN_PROCESO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(auditRepository.findById(1L)).thenReturn(Optional.of(mockAudit));
        when(auditRepository.save(any(Audit.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Audit result = auditService.completar(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("COMPLETADA", result.getEstado());
        assertNotNull(result.getFechaCierre());
    }

    @Test

    void marcarObservacion_EnProceso_CambiaEstado() {
        // Probar que cuando en proceso → cambia estado

        Audit mockAudit = new Audit();
        mockAudit.setId(1L);
        mockAudit.setEstado("EN_PROCESO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(auditRepository.findById(1L)).thenReturn(Optional.of(mockAudit));
        when(auditRepository.save(any(Audit.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Audit result = auditService.marcarObservacion(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("OBSERVACION", result.getEstado());
    }

    @Test

    void obtenerPorId_Existente_RetornaAuditoria() {
        // Probar que cuando existente → retorna auditoria

        Audit mockAudit = new Audit();
        mockAudit.setId(1L);
        mockAudit.setTipoAuditoria("CRUCE_FRONTERIZO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(auditRepository.findById(1L)).thenReturn(Optional.of(mockAudit));

        // --- Ejecutar el método del servicio que se está probando ---
        Audit result = auditService.obtenerPorId(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals("CRUCE_FRONTERIZO", result.getTipoAuditoria());
    }

    @Test

    void auditorInactivo_LanzaExcepcion() {
    // Probar: auditorinactivo lanzaexcepcion

    // Probar: auditorinactivo lanzaexcepcion

        // Crear el DTO con los datos necesarios para la prueba
        AuditDTO dto = new AuditDTO();
        dto.setRutAuditor("9876543-2");
        dto.setTipoAuditoria("CRUCE_FRONTERIZO");
        dto.setEntidad("User");
        dto.setEntidadId(1L);
        dto.setDescripcion("Test");
        dto.setFechaInicio(LocalDateTime.now().minusHours(2));

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setActivo(false);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> auditService.registrar(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("El auditor con RUT 9876543-2 está inactivo en el sistema", ex.getMessage());
    }
}