package com.example.SanitaryService.repository;

import com.example.SanitaryService.model.SanitaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanitaryItemRepository
        extends JpaRepository<SanitaryItem, Long> {

    // Todos los items de una inspección
    // Spring genera: SELECT * FROM sanitary_items WHERE inspection_id = ?
    List<SanitaryItem> findBySanitaryId(Long sanitaryId);

    // Items aprobados de una inspección
    // Spring genera: SELECT * FROM sanitary_items
    //                WHERE inspection_id = ? AND resultado_item = 'APROBADO'
    List<SanitaryItem> findBySanitaryIdAndResultadoItem(
            Long sanitaryId, String resultadoItem);

    // POR RESULTADO
    // Spring genera: SELECT * FROM sanitary_items WHERE resultado_item = ?
    List<SanitaryItem> findByResultadoItem(String resultadoItem);

    // CONTAINING + IGNORE CASE
    // Búsqueda parcial por descripción
    // Spring genera: SELECT * FROM sanitary_items
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<SanitaryItem> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // ORDENAMIENTO
    // Items de una inspección ordenados por descripción
    // Spring genera: SELECT * FROM sanitary_items
    //                WHERE inspection_id = ? ORDER BY descripcion ASC
    List<SanitaryItem> findBySanitaryIdOrderByDescripcionAsc(
            Long sanitaryId);

    // TOP
    // Los últimos 10 items registrados
    // Spring genera: SELECT * FROM sanitary_items ORDER BY id DESC LIMIT 10
    List<SanitaryItem> findTop10ByOrderByIdDesc();

    // COUNT
    // Contar items rechazados de una inspección
    // Spring genera: SELECT COUNT(*) FROM sanitary_items
    //                WHERE inspection_id = ? AND resultado_item = ?
    long countBySanitaryIdAndResultadoItem(
            Long sanitaryId, String resultadoItem);
}