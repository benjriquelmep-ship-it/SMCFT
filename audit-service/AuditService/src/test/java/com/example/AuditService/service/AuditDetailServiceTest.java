package com.example.AuditService.service;

import com.example.AuditService.model.AuditDetail;
import com.example.AuditService.repository.AuditDetailRepository;
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
class AuditDetailServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private AuditDetailService detailService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private AuditDetailRepository detailRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorAuditoria_Existente_RetornaDetalles() {
        // Probar que cuando existente → retorna detalles

        AuditDetail detail = new AuditDetail();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(detailRepository.findByAuditId(1L)).thenReturn(List.of(detail));
        List<AuditDetail> result = detailService.obtenerPorAuditoria(1L);
        assertFalse(result.isEmpty());
    }
}