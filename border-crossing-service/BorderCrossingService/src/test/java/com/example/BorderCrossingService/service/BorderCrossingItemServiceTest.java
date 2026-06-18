package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.model.BorderCrossingItem;
import com.example.BorderCrossingService.repository.BorderCrossingItemRepository;
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
class BorderCrossingItemServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private BorderCrossingItemService crossingItemService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private BorderCrossingItemRepository crossingItemRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorCruce_Existente_RetornaItems() {
        // Probar que cuando existente → retorna items

        BorderCrossingItem item = new BorderCrossingItem();
        item.setId(1L);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(crossingItemRepository.findByBorderCrossingId(1L)).thenReturn(List.of(item));
        List<BorderCrossingItem> result = crossingItemService.obtenerPorCruce(1L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}