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

    // Buscar por nombre exacto
    Optional<ItemCategory> findByNombre(String nombre);

    // Verificar si existe una categoría con ese nombre
    boolean existsByNombre(String nombre);

    // Solo categorías activas
    List<ItemCategory> findByActivoTrue();

    // Solo categorías inactivas
    List<ItemCategory> findByActivoFalse();

    // Categorías que requieren declaración y están activas
    List<ItemCategory> findByRequiereDeclaracionTrueAndActivoTrue();

    // Categorías que NO requieren declaración y están activas
    List<ItemCategory> findByRequiereDeclaracionFalseAndActivoTrue();

    // Búsqueda parcial por nombre
    List<ItemCategory> findByNombreContainingIgnoreCase(String texto);

    // Categorías con límite menor o igual a un valor
    List<ItemCategory> findByLimiteValorUsdLessThanEqual(
            BigDecimal limite);

    // Categorías activas ordenadas por nombre A→Z
    List<ItemCategory> findByActivoTrueOrderByNombreAsc();

    // Las últimas 5 categorías registradas
    List<ItemCategory> findTop5ByOrderByIdDesc();
}