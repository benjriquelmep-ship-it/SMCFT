package com.example.EntryService.service;

import com.example.EntryService.dto.EntryDTO;
import com.example.EntryService.dto.VehicleResponseDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.repository.EntryRepository;
import java.time.LocalDateTime;
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
class EntryServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private EntryService entryService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private EntryRepository entryRepository;
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

    void registrar_VehiculoFueraDelPais_RegistraEntrada() {
        // Probar que cuando vehiculo fuera del pais → registra entrada

        // Crear el DTO con los datos necesarios para la prueba
        EntryDTO dto = new EntryDTO();
        dto.setPatente("ABC-123");
        dto.setRutConductor("12345678-9");
        dto.setPaisOrigen("Argentina");
        dto.setPasoFronterizo("Los Libertadores");
        dto.setFechaIngreso(LocalDateTime.now().minusHours(1));
        dto.setTipoIngreso("RETORNO");
        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        vehicleResponse.setEstado("FUERA_DEL_PAIS");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/vehicles/patente/{patente}", "ABC-123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(entryRepository.save(any(Entry.class))).thenAnswer(i -> {
            Entry e = i.getArgument(0);
            e.setId(1L);
            return e;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Entry result = entryService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
assertEquals("PENDIENTE", result.getEstado());

        // Verificar la interacción con el repositorio
        verify(entryRepository).save(any(Entry.class));
    }
    @Test

    void registrar_VehiculoEnTerritorioNacional_LanzaExcepcion() {
        // Probar que cuando vehiculo en territorio nacional → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        EntryDTO dto = new EntryDTO();
        dto.setPatente("ABC-123");
        dto.setFechaIngreso(LocalDateTime.now().minusHours(1));
        dto.setTipoIngreso("RETORNO");
        VehicleResponseDTO vehicleResponse = new VehicleResponseDTO();
        vehicleResponse.setEstado("EN_TERRITORIO_NACIONAL");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/vehicles/patente/{patente}", "ABC-123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(VehicleResponseDTO.class)).thenReturn(Mono.just(vehicleResponse));

        // Llamar al servicio y capturar la excepción lanzada

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> entryService.registrar(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("FUERA_DEL_PAIS"));
    }
}