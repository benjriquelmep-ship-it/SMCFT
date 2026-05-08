// repository/EntryItemRepository.java

package com.example.EntryService.repository;

import com.example.EntryService.model.EntryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntryItemRepository
        extends JpaRepository<EntryItem, Long> {

    // Todos los items de un ingreso
    // Spring genera: SELECT * FROM entry_items WHERE entry_id = ?
    List<EntryItem> findByEntryId(Long entryId);

    // Items aprobados de un ingreso
    // Spring genera: SELECT * FROM entry_items
    //                WHERE entry_id = ? AND aprobado = true
    List<EntryItem> findByEntryIdAndAprobadoTrue(Long entryId);

    // Items no aprobados de un ingreso
    // Spring genera: SELECT * FROM entry_items
    //                WHERE entry_id = ? AND aprobado = false
    List<EntryItem> findByEntryIdAndAprobadoFalse(Long entryId);

    // BOOLEANOS
    // Spring genera: SELECT * FROM entry_items WHERE aprobado = true
    List<EntryItem> findByAprobadoTrue();
    List<EntryItem> findByAprobadoFalse();

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por descripción
    // Spring genera: SELECT * FROM entry_items
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<EntryItem> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // ORDENAMIENTO
    // Items de un ingreso ordenados de mayor a menor valor
    // Spring genera: SELECT * FROM entry_items
    //                WHERE entry_id = ? ORDER BY valor_usd DESC
    List<EntryItem> findByEntryIdOrderByValorUsdDesc(Long entryId);

    // TOP
    // Los últimos 10 items registrados
    // Spring genera: SELECT * FROM entry_items ORDER BY id DESC LIMIT 10
    List<EntryItem> findTop10ByOrderByIdDesc();
}