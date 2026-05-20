// Accede a la tabla items en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.ItemCategoryService.repository;

import com.example.ItemCategoryService.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Devuelve todos los items de una categoría específica
    // Incluye activos e inactivos
    List<Item> findByCategoryId(Long categoryId);

    // Devuelve solo los items ACTIVOS de una categoría específica
    List<Item> findByCategoryIdAndActivoTrue(Long categoryId);

    // Devuelve TODOS los items activos del sistema
    List<Item> findByActivoTrue();

    // Devuelve TODOS los items inactivos del sistema
    List<Item> findByActivoFalse();

    // Busca items cuyo nombre contenga el texto buscado
    List<Item> findByNombreContainingIgnoreCase(String texto);

    // Devuelve todos los items que se miden en una unidad específica
    List<Item> findByUnidad(String unidad);

    // Devuelve los items de una categoría ordenados alfabéticamente
    // Útil para mostrar una lista ordenada en el frontend
    List<Item> findByCategoryIdOrderByNombreAsc(Long categoryId);

    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear los últimos items creados en el sistema
    List<Item> findTop10ByOrderByIdDesc();
}