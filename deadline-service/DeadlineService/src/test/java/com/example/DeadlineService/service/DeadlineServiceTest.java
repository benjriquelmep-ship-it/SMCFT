package com.example.DeadlineService.service;

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

import com.example.DeadlineService.dto.DeadlineDTO;
import com.example.DeadlineService.dto.EntryResponseDTO;
import com.example.DeadlineService.model.Deadline;
import com.example.DeadlineService.repository.DeadlineRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class DeadlineServiceTest {

    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private DeadlineService deadlineService;

    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private DeadlineRepository deadlineRepository;

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

    void registrar_DatosValidos_RegistraPlazo() {
        // Probar que cuando datos validos → registra plazo

        // Crear el DTO con los datos necesarios para la prueba
        DeadlineDTO dto = new DeadlineDTO();
        dto.setPatente("ABC-123");
        dto.setRutConductor("12345678-9");
        dto.setEntryId(1L);
        dto.setFechaIngreso(LocalDateTime.now().minusDays(5));
        dto.setFechaLimite(LocalDateTime.now().plusDays(80));
        dto.setTipo("ADMISION_TEMPORAL");
        dto.setObservaciones("Test");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EntryResponseDTO.class)).thenReturn(Mono.just(new EntryResponseDTO()));

        when(deadlineRepository.save(any(Deadline.class))).thenAnswer(i -> {
            Deadline d = i.getArgument(0);
            d.setId(1L);
            return d;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Deadline result = deadlineService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVO", result.getEstado());
        assertEquals("ABC-123", result.getPatente());
    }

    @Test

    void cerrar_EstadoActivo_CambiaEstado() {
        // Probar que cuando estado activo → cambia estado

        Deadline mockDeadline = new Deadline();
        mockDeadline.setId(1L);
        mockDeadline.setEstado("ACTIVO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(deadlineRepository.findById(1L)).thenReturn(Optional.of(mockDeadline));
        when(deadlineRepository.save(any(Deadline.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Deadline result = deadlineService.cerrar(1L, "Motivo del cierre");

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("CERRADO", result.getEstado());
        assertEquals("Motivo del cierre", result.getObservaciones());
    }

    @Test

    void vencer_EstadoActivo_CambiaEstado() {
        // Probar que cuando estado activo → cambia estado

        Deadline mockDeadline = new Deadline();
        mockDeadline.setId(1L);
        mockDeadline.setEstado("ACTIVO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(deadlineRepository.findById(1L)).thenReturn(Optional.of(mockDeadline));
        when(deadlineRepository.save(any(Deadline.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Deadline result = deadlineService.vencer(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("VENCIDO", result.getEstado());
    }

    @Test

    void obtenerPorId_Existente_RetornaDeadline() {
        // Probar que cuando existente → retorna deadline

        Deadline mockDeadline = new Deadline();
        mockDeadline.setId(1L);
        mockDeadline.setPatente("ABC-123");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(deadlineRepository.findById(1L)).thenReturn(Optional.of(mockDeadline));

        // --- Ejecutar el método del servicio que se está probando ---
        Deadline result = deadlineService.obtenerPorId(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals("ABC-123", result.getPatente());
    }

    @Test

    void obtenerPorId_NoExistente_LanzaExcepcion() {
        // Probar que cuando no existente → lanza excepcion

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(deadlineRepository.findById(99L)).thenReturn(Optional.empty());

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> deadlineService.obtenerPorId(99L));

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("Deadline no encontrado con id: 99", ex.getMessage());
    }
}