package com.example.ReportService.service;

import com.example.ReportService.model.ReportDetail;
import com.example.ReportService.repository.ReportDetailRepository;
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
class ReportDetailServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private ReportDetailService detailService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private ReportDetailRepository detailRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorReporte_Existente_RetornaDetalles() {
        // Probar que cuando existente → retorna detalles

        ReportDetail detail = new ReportDetail();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(detailRepository.findByReportId(1L)).thenReturn(List.of(detail));
        List<ReportDetail> result = detailService.obtenerPorReporte(1L);
        assertFalse(result.isEmpty());
    }
}