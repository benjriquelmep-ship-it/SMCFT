package com.example.SanitaryService.service;

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

import com.example.SanitaryService.dto.SanitaryDTO;
import com.example.SanitaryService.dto.VehicleResponseDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.repository.SanitaryRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class SanitaryServiceTest {

    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private SanitaryService sanitaryService;

    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private SanitaryRepository sanitaryRepository;

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

    void registrar_DatosValidos_RegistraControl() {
        // Probar que cuando datos validos → registra control

        // Crear el DTO con los datos necesarios para la prueba
        SanitaryDTO dto = new SanitaryDTO();
        dto.setPatente("ABC-123");
        dto.setRutConductor("12345678-9");
        dto.setRutInspector("9876543-2");
        dto.setPasoFronterizo("Los Libertadores");
        dto.setFechaInspeccion(LocalDateTime.now().minusHours(2));
        dto.setObservaciones("Test inspection");

        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));

        when(sanitaryRepository.save(any(Sanitary.class))).thenAnswer(i -> {
            Sanitary s = i.getArgument(0);
            s.setId(1L);
            return s;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Sanitary result = sanitaryService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDIENTE", result.getResultado());
        assertEquals("ABC-123", result.getPatente());
    }

    @Test

    void aprobar_Pendiente_CambiaEstado() {
        // Probar que cuando pendiente → cambia estado

        Sanitary mockSanitary = new Sanitary();
        mockSanitary.setId(1L);
        mockSanitary.setResultado("PENDIENTE");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(sanitaryRepository.findById(1L)).thenReturn(Optional.of(mockSanitary));
        when(sanitaryRepository.save(any(Sanitary.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Sanitary result = sanitaryService.aprobar(1L, "Todo en orden");

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("APROBADO", result.getResultado());
    }

    @Test

    void rechazar_Pendiente_CambiaEstado() {
        // Probar que cuando pendiente → cambia estado

        Sanitary mockSanitary = new Sanitary();
        mockSanitary.setId(1L);
        mockSanitary.setResultado("PENDIENTE");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(sanitaryRepository.findById(1L)).thenReturn(Optional.of(mockSanitary));
        when(sanitaryRepository.save(any(Sanitary.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Sanitary result = sanitaryService.rechazar(1L, "No cumple normas");

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("RECHAZADO", result.getResultado());
    }

    @Test

    void obtenerPorId_Existente_RetornaSanitary() {
        // Probar que cuando existente → retorna sanitary

        Sanitary mockSanitary = new Sanitary();
        mockSanitary.setId(1L);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(sanitaryRepository.findById(1L)).thenReturn(Optional.of(mockSanitary));

        // --- Ejecutar el método del servicio que se está probando ---
        Sanitary result = sanitaryService.obtenerPorId(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}