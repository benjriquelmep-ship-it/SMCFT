// repository/ItemCategoryRepository.java
// Consultas derivadas del apunte integradas

package com.example.ItemCategoryService.repository;

import com.example.ItemCategoryService.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCategoryRepository
        extends JpaRepository<ItemCategory, Long> {

    // IGUALDAD BÁSICA
    // Buscar por nombre exacto
    // Spring genera: SELECT * FROM item_categories WHERE nombre = ?
    Optional<ItemCategory> findByNombre(String nombre);

    // EXISTENCIA
    // Verificar si existe una categoría con ese nombre
    // Spring genera: SELECT COUNT(*) > 0 FROM item_categories WHERE nombre = ?
    boolean existsByNombre(String nombre);

    // BOOLEANOS
    // Solo categorías activas
    // Spring genera: SELECT * FROM item_categories WHERE activo = true
    List<ItemCategory> findByActivoTrue();

    // Solo categorías inactivas
    // Spring genera: SELECT * FROM item_categories WHERE activo = false
    List<ItemCategory> findByActivoFalse();

    // AND CON BOOLEANO
    // Categorías que requieren declaración y están activas
    // Spring genera: SELECT * FROM item_categories
    //                WHERE requiere_declaracion = true AND activo = true
    List<ItemCategory> findByRequiereDeclaracionTrueAndActivoTrue();

    // Categorías que NO requieren declaración y están activas
    List<ItemCategory> findByRequiereDeclaracionFalseAndActivoTrue();

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por nombre
    // Spring genera: SELECT * FROM item_categories
    //                WHERE LOWER(nombre) LIKE LOWER('%texto%')
    List<ItemCategory> findByNombreContainingIgnoreCase(String texto);

    // COMPARACIÓN NUMÉRICA
    // Categorías con límite menor o igual a un valor
    // Spring genera: SELECT * FROM item_categories
    //                WHERE limite_valor_usd <= ?
    List<ItemCategory> findByLimiteValorUsdLessThanEqual(
            BigDecimal limite);

    // ORDENAMIENTO
    // Categorías activas ordenadas por nombre A→Z
    // Spring genera: SELECT * FROM item_categories
    //                WHERE activo = true ORDER BY nombre ASC
    List<ItemCategory> findByActivoTrueOrderByNombreAsc();

    // TOP
    // Las últimas 5 categorías registradas
    // Spring genera: SELECT * FROM item_categories ORDER BY id DESC LIMIT 5
    List<ItemCategory> findTop5ByOrderByIdDesc();
}