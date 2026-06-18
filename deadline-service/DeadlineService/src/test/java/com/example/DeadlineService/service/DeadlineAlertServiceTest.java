package com.example.DeadlineService.service;

import com.example.DeadlineService.model.DeadlineAlert;
import com.example.DeadlineService.repository.DeadlineAlertRepository;
import java.util.List;
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
class DeadlineAlertServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private DeadlineAlertService alertService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private DeadlineAlertRepository alertRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorDeadline_Existente_RetornaAlertas() {
        // Probar que cuando existente → retorna alertas

        DeadlineAlert alert = new DeadlineAlert();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(alertRepository.findByDeadlineId(1L)).thenReturn(List.of(alert));
        List<DeadlineAlert> result = alertService.obtenerPorDeadline(1L);
        assertFalse(result.isEmpty());
    }
}