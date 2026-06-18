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
}