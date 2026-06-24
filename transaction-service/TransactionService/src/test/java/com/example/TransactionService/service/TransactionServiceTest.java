package com.example.TransactionService.service;

import com.example.TransactionService.dto.TransactionDTO;
import com.example.TransactionService.dto.UserResponseDTO;
import com.example.TransactionService.model.Transaction;
import com.example.TransactionService.repository.TransactionRepository;
import java.math.BigDecimal;
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
class TransactionServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private TransactionService transactionService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private TransactionRepository transactionRepository;
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

    void registrar_DatosValidos_RegistraTransaccion() {
        // Probar que cuando datos validos → registra transaccion

        // Crear el DTO con los datos necesarios para la prueba
        TransactionDTO dto = new TransactionDTO();
        dto.setTipo("PAGO_TASAS");
        dto.setMontoTotal(new BigDecimal("50000"));
        dto.setRutUsuario("12345678-9");

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/rut/{rut}", "12345678-9")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Transaction result = transactionService.registrar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void registrar_MontoMenorOIgualACero_LanzaExcepcion() {
        // Probar que cuando el monto es <= 0 → lanza excepcion

        TransactionDTO dto = new TransactionDTO();
        dto.setTipo("PAGO_TASAS");
        dto.setMontoTotal(new BigDecimal("0"));
        dto.setRutUsuario("12345678-9");

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setActivo(true);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/v1/users/rut/{rut}", "12345678-9")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponseDTO.class)).thenReturn(Mono.just(userResponse));

        // --- Ejecutar y verificar que lanza excepcion ---
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.registrar(dto));
        assertEquals("El monto total debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void completar_EstadoPendiente_CambiaEstado() {
        // Probar que cuando el estado es PENDIENTE → cambia a COMPLETADA

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("PENDIENTE");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar ---
        Transaction result = transactionService.completar(1L);

        // --- Verificar ---
        assertNotNull(result);
        assertEquals("COMPLETADA", result.getEstado());
    }

    @Test
    void completar_EstadoNoPendiente_LanzaExcepcion() {
        // Probar que cuando el estado no es PENDIENTE → lanza excepcion

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("COMPLETADA");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));

        // --- Ejecutar y verificar ---
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.completar(1L));
        assertEquals("Solo se pueden completar transacciones PENDIENTES. Estado actual: COMPLETADA",
                exception.getMessage());
    }

    @Test
    void rechazar_EstadoPendiente_CambiaEstado() {
        // Probar que cuando el estado es PENDIENTE → cambia a RECHAZADA

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("PENDIENTE");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar ---
        Transaction result = transactionService.rechazar(1L);

        // --- Verificar ---
        assertNotNull(result);
        assertEquals("RECHAZADA", result.getEstado());
    }

    @Test
    void rechazar_EstadoNoPendiente_LanzaExcepcion() {
        // Probar que cuando el estado no es PENDIENTE → lanza excepcion

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("COMPLETADA");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));

        // --- Ejecutar y verificar ---
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.rechazar(1L));
        assertEquals("Solo se pueden rechazar transacciones PENDIENTES. Estado actual: COMPLETADA",
                exception.getMessage());
    }

    @Test
    void anular_EstadoPendiente_CambiaEstado() {
        // Probar que cuando el estado es PENDIENTE → cambia a ANULADA

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("PENDIENTE");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar ---
        Transaction result = transactionService.anular(1L);

        // --- Verificar ---
        assertNotNull(result);
        assertEquals("ANULADA", result.getEstado());
    }

    @Test
    void anular_EstadoAnulada_LanzaExcepcion() {
        // Probar que cuando ya esta ANULADA → lanza excepcion

        Transaction t = new Transaction();
        t.setId(1L);
        t.setEstado("ANULADA");
        t.setRutUsuario("12345678-9");
        t.setTipo("PAGO_TASAS");
        t.setMontoTotal(new BigDecimal("50000"));

        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(t));

        // --- Ejecutar y verificar ---
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.anular(1L));
        assertEquals("La transacción ya está anulada", exception.getMessage());
    }
}