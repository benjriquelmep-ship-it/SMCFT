package com.example.VehicleService.service;

import com.example.VehicleService.dto.UserResponseDTO;
import com.example.VehicleService.dto.VehicleDTO;
import com.example.VehicleService.model.Vehicle;
import com.example.VehicleService.repository.VehicleRepository;
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
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private VehicleService vehicleService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private VehicleRepository vehicleRepository;
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

    void registrar_DatosValidos_RegistraVehiculo() {
        // Probar que cuando datos validos → registra vehiculo

        // Crear el DTO con los datos necesarios para la prueba
        VehicleDTO dto = new VehicleDTO();
        dto.setPatente("ABC-123");
        dto.setMarca("Toyota");
        dto.setModelo("Corolla");
        dto.setAnio(2024);
        dto.setTipoVehiculo("AUTOMOVIL");
        dto.setRutPropietario("12345678-9");
        when(vehicleRepository.existsByPatente("ABC-123")).thenReturn(false);
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/rut/{rut}", "12345678-9")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // --- Ejecutar el método del servicio que se está probando ---
        Vehicle result = vehicleService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
    @Test

    void registrar_PatenteDuplicada_LanzaExcepcion() {
        // Probar que cuando patente duplicada → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        VehicleDTO dto = new VehicleDTO();
        dto.setPatente("ABC-123");
        when(vehicleRepository.existsByPatente("ABC-123")).thenReturn(true);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> vehicleService.registrar(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("patente"));

        // Verificar que el repositorio NO haya sido invocado
        verify(vehicleRepository, never()).save(any());
    }
    @Test

    void registrar_AnioFuturo_LanzaExcepcion() {
        // Probar que cuando anio futuro → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        VehicleDTO dto = new VehicleDTO();
        dto.setPatente("ABC-123");
        dto.setAnio(2099);
        when(vehicleRepository.existsByPatente("ABC-123")).thenReturn(false);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> vehicleService.registrar(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("año"));

        // Verificar que el repositorio NO haya sido invocado
        verify(vehicleRepository, never()).save(any());
    }
    @Test

    void actualizarEstado_EstadoValido_ActualizaCorrectamente() {
        // Probar que cuando estado valido → actualiza correctamente

        Vehicle vehicle = new Vehicle();
        vehicle.setPatente("ABC-123");
        vehicle.setEstado("EN_TERRITORIO_NACIONAL");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(vehicleRepository.findByPatente("ABC-123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Vehicle result = vehicleService.actualizarEstado("ABC-123", "FUERA_DEL_PAIS");

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("FUERA_DEL_PAIS", result.getEstado());
    }
    @Test

    void actualizarEstado_EstadoInvalido_LanzaExcepcion() {
        // Probar que cuando estado invalido → lanza excepcion

        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class,

            () -> vehicleService.actualizarEstado("ABC-123", "ESTADO_INVALIDO"));

        assertTrue(ex.getMessage().contains("Estado inválido"));

        // Verificar que el repositorio NO haya sido invocado
        verify(vehicleRepository, never()).save(any());
    }
}