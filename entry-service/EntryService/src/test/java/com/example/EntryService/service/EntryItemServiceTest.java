package com.example.EntryService.service;

import com.example.EntryService.model.EntryItem;
import com.example.EntryService.repository.EntryItemRepository;
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
class EntryItemServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private EntryItemService entryItemService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private EntryItemRepository entryItemRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorIngreso_Existente_RetornaItems() {
        // Probar que cuando existente → retorna items

        EntryItem item = new EntryItem();
        item.setId(1L);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(entryItemRepository.findByEntryId(1L)).thenReturn(List.of(item));

        // --- Ejecutar el método del servicio que se está probando ---
        List<EntryItem> result = entryItemService.obtenerPorIngreso(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertFalse(result.isEmpty());
    }
}