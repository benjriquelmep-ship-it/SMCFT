package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.dto.BorderCrossingDTO;
import com.example.BorderCrossingService.dto.VehicleResponseDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.repository.BorderCrossingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class BorderCrossingServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private BorderCrossingService crossingService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private BorderCrossingRepository crossingRepository;
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
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void registrar_VehiculoEnTerritorioNacional_RegistraCruce() {
        // Probar que cuando vehiculo en territorio nacional → registra cruce

        // Crear el DTO con los datos necesarios para la prueba
        BorderCrossingDTO dto = new BorderCrossingDTO();
        dto.setPatente("ABC-123");
        dto.setRutConductor("12345678-9");
        dto.setPaisDestino("Argentina");
        dto.setPasoFronterizo("Los Libertadores");
        dto.setFechaCruce(LocalDateTime.now().minusHours(1));
        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        vehicleResponse.setEstado("EN_TERRITORIO_NACIONAL");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/vehicles/patente/{patente}", "ABC-123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(crossingRepository.save(any(BorderCrossing.class))).thenAnswer(i -> {
            BorderCrossing bc = i.getArgument(0);
            bc.setId(1L);
            return bc;
        });
        BorderCrossing result = crossingService.registrar(dto);
        assertNotNull(result);
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals("ABC-123", result.getPatente());
    }
    @Test

    void registrar_VehiculoFueraDelPais_LanzaExcepcion() {
        // Probar que cuando vehiculo fuera del pais → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        BorderCrossingDTO dto = new BorderCrossingDTO();
        dto.setPatente("ABC-123");
        dto.setFechaCruce(LocalDateTime.now().minusHours(1));
        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        vehicleResponse.setEstado("FUERA_DEL_PAIS");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/vehicles/patente/{patente}", "ABC-123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));
        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class, () -> crossingService.registrar(dto));
        assertTrue(ex.getMessage().contains("no está en territorio nacional"));
    }
    @Test

    void registrar_FechaFutura_LanzaExcepcion() {
        // Probar que cuando fecha futura → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        BorderCrossingDTO dto = new BorderCrossingDTO();
        dto.setPatente("ABC-123");
        dto.setFechaCruce(LocalDateTime.now().plusDays(1));
        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        vehicleResponse.setEstado("EN_TERRITORIO_NACIONAL");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/vehicles/patente/{patente}", "ABC-123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));
        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class, () -> crossingService.registrar(dto));
        assertTrue(ex.getMessage().contains("futura"));
    }
    @Test

    void autorizar_CrucePendiente_CambiaEstadoAAutorizado() {
        // Probar que cuando cruce pendiente → cambia estado a autorizado

        BorderCrossing cruce = new BorderCrossing();
        cruce.setId(1L);
        cruce.setEstado("PENDIENTE");
        cruce.setPatente("ABC-123");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(crossingRepository.findById(1L)).thenReturn(Optional.of(cruce));
        when(crossingRepository.save(any(BorderCrossing.class))).thenAnswer(i -> i.getArgument(0));
        BorderCrossing result = crossingService.autorizar(1L, "11111111-1", "Todo en orden");
        assertEquals("AUTORIZADO", result.getEstado());
        assertEquals("11111111-1", result.getRutFiscalizador());
        assertEquals("Todo en orden", result.getObservaciones());
    }
    @Test

    void rechazar_CrucePendiente_CambiaEstadoARechazado() {
        // Probar que cuando cruce pendiente → cambia estado a rechazado

        BorderCrossing cruce = new BorderCrossing();
        cruce.setId(1L);
        cruce.setEstado("PENDIENTE");
        cruce.setPatente("ABC-123");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(crossingRepository.findById(1L)).thenReturn(Optional.of(cruce));
        when(crossingRepository.save(any(BorderCrossing.class))).thenAnswer(i -> i.getArgument(0));
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
        BorderCrossing result = crossingService.rechazar(1L, "11111111-1", "Documentación incompleta");
        assertEquals("RECHAZADO", result.getEstado());
    }
    @Test

    void autorizar_CruceNoPendiente_LanzaExcepcion() {
        // Probar que cuando cruce no pendiente → lanza excepcion

        BorderCrossing cruce = new BorderCrossing();
        cruce.setId(1L);
        cruce.setEstado("AUTORIZADO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(crossingRepository.findById(1L)).thenReturn(Optional.of(cruce));
        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class, () -> crossingService.autorizar(1L, "11111111-1", ""));
        assertTrue(ex.getMessage().contains("PENDIENTE"));
    }
}