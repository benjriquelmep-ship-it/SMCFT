package com.example.ItemCategoryService.service;

import com.example.ItemCategoryService.dto.ItemCategoryDTO;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.repository.ItemCategoryRepository;
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
class ItemCategoryServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private ItemCategoryService categoryService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private ItemCategoryRepository categoryRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void crear_DatosValidos_CreaCategoria() {
        // Probar que cuando datos validos → crea categoria

        // Crear el DTO con los datos necesarios para la prueba
        ItemCategoryDTO dto = new ItemCategoryDTO();
        dto.setNombre("Electrónicos");
        dto.setDescripcion("Productos electrónicos");
        when(categoryRepository.existsByNombre("Electrónicos")).thenReturn(false);
        when(categoryRepository.save(any(ItemCategory.class))).thenAnswer(i -> {
            ItemCategory c = i.getArgument(0);
            c.setId(1L);
            return c;
        });
        ItemCategory result = categoryService.crear(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
    @Test

    void crear_NombreDuplicado_LanzaExcepcion() {
        // Probar que cuando nombre duplicado → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        ItemCategoryDTO dto = new ItemCategoryDTO();
        dto.setNombre("Electrónicos");
        when(categoryRepository.existsByNombre("Electrónicos")).thenReturn(true);
        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoryService.crear(dto));
        assertTrue(ex.getMessage().contains("nombre"));
    }
}