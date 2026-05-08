// repository/BorderCrossingItemRepository.java

package com.example.BorderCrossingService.repository;

import com.example.BorderCrossingService.model.BorderCrossingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorderCrossingItemRepository
        extends JpaRepository<BorderCrossingItem, Long> {

    // IGUALDAD BÁSICA
    // Todos los items de un cruce
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE border_crossing_id = ?
    List<BorderCrossingItem> findByBorderCrossingId(Long borderCrossingId);

    // AND CON BOOLEANO
    // Items aprobados de un cruce
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE border_crossing_id = ? AND aprobado = true
    List<BorderCrossingItem> findByBorderCrossingIdAndAprobadoTrue(
            Long borderCrossingId);

    // Items no aprobados de un cruce
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE border_crossing_id = ? AND aprobado = false
    List<BorderCrossingItem> findByBorderCrossingIdAndAprobadoFalse(
            Long borderCrossingId);

    // POR CATEGORÍA
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE categoria_id = ?
    List<BorderCrossingItem> findByCategoriaId(Long categoriaId);

    // BOOLEANOS
    // Spring genera: SELECT * FROM border_crossing_items WHERE aprobado = true
    List<BorderCrossingItem> findByAprobadoTrue();

    // Spring genera: SELECT * FROM border_crossing_items WHERE aprobado = false
    List<BorderCrossingItem> findByAprobadoFalse();

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por descripción del item
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<BorderCrossingItem> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // ORDENAMIENTO
    // Items de un cruce ordenados de mayor a menor valor en USD
    // Spring genera: SELECT * FROM border_crossing_items
    //                WHERE border_crossing_id = ? ORDER BY valor_usd DESC
    List<BorderCrossingItem> findByBorderCrossingIdOrderByValorUsdDesc(
            Long borderCrossingId);

    // TOP
    // Los últimos 10 items registrados en el sistema
    // Spring genera: SELECT * FROM border_crossing_items ORDER BY id DESC LIMIT 10
    List<BorderCrossingItem> findTop10ByOrderByIdDesc();
}