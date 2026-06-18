package com.example.TransactionService.service;

import com.example.TransactionService.model.TransactionDetail;
import com.example.TransactionService.repository.TransactionDetailRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class TransactionDetailServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private TransactionDetailService detailService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private TransactionDetailRepository detailRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorTransaccion_Existente_RetornaDetalles() {
        // Probar que cuando existente → retorna detalles

        TransactionDetail detail = new TransactionDetail();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(detailRepository.findByTransactionId(1L)).thenReturn(List.of(detail));
        List<TransactionDetail> result = detailService.obtenerPorTransaccion(1L);
        assertFalse(result.isEmpty());
    }
}