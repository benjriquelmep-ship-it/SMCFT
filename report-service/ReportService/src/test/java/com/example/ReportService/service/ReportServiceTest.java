package com.example.ReportService.service;

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
import java.util.Collections;
import java.time.LocalDateTime;

import com.example.ReportService.dto.ReportDTO;
import com.example.ReportService.model.Report;
import com.example.ReportService.repository.ReportRepository;
import com.example.ReportService.repository.ReportDetailRepository;
import org.springframework.web.reactive.function.client.WebClient;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private ReportService reportService;

    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportDetailRepository detailRepository;

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

    void generar_DatosValidosSinCruces_CreaReporte() {
        // Probar que cuando datos validos sin cruces → crea reporte

        // Crear el DTO con los datos necesarios para la prueba
        ReportDTO dto = new ReportDTO();
        dto.setTitulo("Reporte Mensual");
        dto.setTipoReporte("INGRESOS");
        dto.setFechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setFechaFin(LocalDateTime.of(2026, 4, 30, 23, 59));
        dto.setGeneradoPor("12345678-9");
        dto.setObservaciones("Test");

        when(reportRepository.save(any(Report.class))).thenAnswer(i -> {
            Report r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Report result = reportService.generar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("COMPLETADO", result.getEstado());
    }

    @Test

    void completar_Generando_CambiaEstado() {
        // Probar que cuando generando → cambia estado

        Report mockReport = new Report();
        mockReport.setId(1L);
        mockReport.setEstado("GENERANDO");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(reportRepository.findById(1L)).thenReturn(Optional.of(mockReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        Report result = reportService.completar(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertEquals("COMPLETADO", result.getEstado());
    }

    @Test

    void obtenerPorId_Existente_RetornaReporte() {
        // Probar que cuando existente → retorna reporte

        Report mockReport = new Report();
        mockReport.setId(1L);
        mockReport.setTitulo("Reporte Mensual");

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(reportRepository.findById(1L)).thenReturn(Optional.of(mockReport));

        // --- Ejecutar el método del servicio que se está probando ---
        Report result = reportService.obtenerPorId(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals("Reporte Mensual", result.getTitulo());
    }
}