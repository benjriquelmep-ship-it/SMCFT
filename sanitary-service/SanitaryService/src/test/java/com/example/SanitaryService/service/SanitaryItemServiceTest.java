package com.example.SanitaryService.service;

import com.example.SanitaryService.model.SanitaryItem;
import com.example.SanitaryService.repository.SanitaryItemRepository;
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
class SanitaryItemServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private SanitaryItemService itemService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private SanitaryItemRepository itemRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorInspeccion_Existente_RetornaItems() {
        // Probar que cuando existente → retorna items

        SanitaryItem item = new SanitaryItem();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(itemRepository.findBySanitaryId(1L)).thenReturn(List.of(item));

        // --- Ejecutar el método del servicio que se está probando ---
        List<SanitaryItem> result = itemService.obtenerPorInspeccion(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertFalse(result.isEmpty());
    }
}