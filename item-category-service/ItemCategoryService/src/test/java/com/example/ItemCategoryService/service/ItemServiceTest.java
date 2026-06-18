package com.example.ItemCategoryService.service;

import com.example.ItemCategoryService.dto.ItemDTO;
import com.example.ItemCategoryService.model.Item;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.repository.ItemRepository;
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
class ItemServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private ItemService itemService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemCategoryService categoryService;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void crear_DatosValidos_CreaItem() {
        // Probar que cuando datos validos → crea item

        // Crear el DTO con los datos necesarios para la prueba
        ItemDTO dto = new ItemDTO();
        dto.setNombre("Laptop");
        dto.setCategoriaId(1L);
        ItemCategory categoria = new ItemCategory();
        categoria.setId(1L);
        categoria.setActivo(true);
        when(categoryService.obtenerPorId(1L)).thenReturn(categoria);
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> {
            Item item = i.getArgument(0);
            item.setId(1L);
            return item;
        });

        // --- Ejecutar el método del servicio que se está probando ---
        Item result = itemService.crear(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}