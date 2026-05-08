// repository/ItemRepository.java

package com.example.ItemCategoryService.repository;

import com.example.ItemCategoryService.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Todos los items de una categoría
    // Spring genera: SELECT * FROM items WHERE categoria_id = ?
    List<Item> findByCategoryId(Long categoryId);

    // Items activos de una categoría
    // Spring genera: SELECT * FROM items
    //                WHERE categoria_id = ? AND activo = true
    List<Item> findByCategoryIdAndActivoTrue(Long categoryId);

    // BOOLEANOS
    // Spring genera: SELECT * FROM items WHERE activo = true
    List<Item> findByActivoTrue();
    List<Item> findByActivoFalse();

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por nombre
    // Spring genera: SELECT * FROM items
    //                WHERE LOWER(nombre) LIKE LOWER('%texto%')
    List<Item> findByNombreContainingIgnoreCase(String texto);

    // IGUALDAD CON UNIDAD
    // Spring genera: SELECT * FROM items WHERE unidad = ?
    List<Item> findByUnidad(String unidad);

    // ORDENAMIENTO
    // Items de una categoría ordenados por nombre
    // Spring genera: SELECT * FROM items
    //                WHERE categoria_id = ? ORDER BY nombre ASC
    List<Item> findByCategoryIdOrderByNombreAsc(Long categoryId);

    // TOP
    // Los últimos 10 items registrados
    // Spring genera: SELECT * FROM items ORDER BY id DESC LIMIT 10
    List<Item> findTop10ByOrderByIdDesc();
}