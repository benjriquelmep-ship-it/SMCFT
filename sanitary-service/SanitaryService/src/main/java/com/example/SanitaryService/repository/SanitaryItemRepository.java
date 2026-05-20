// Accede a la tabla sanitary_items en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.SanitaryService.repository;

import com.example.SanitaryService.model.SanitaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanitaryItemRepository
        extends JpaRepository<SanitaryItem, Long> {

    // Devuelve todos los items que pertenecen a una inspección específica
    List<SanitaryItem> findBySanitaryId(Long sanitaryId);

    // Devuelve items de una inspección con un resultado específico
    List<SanitaryItem> findBySanitaryIdAndResultadoItem(
            Long sanitaryId, String resultadoItem);

    // Devuelve todos los items del sistema con un resultado específico
    List<SanitaryItem> findByResultadoItem(String resultadoItem);

    // Busca items cuya descripción contenga el texto buscado
    List<SanitaryItem> findByDescripcionContainingIgnoreCase(
            String descripcion);


    // Devuelve los items de una inspección ordenados alfabéticamente
    // Útil para mostrar los items en orden en el frontend
    List<SanitaryItem> findBySanitaryIdOrderByDescripcionAsc(
            Long sanitaryId);

    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear en tiempo real los últimos objetos inspeccionados
    List<SanitaryItem> findTop10ByOrderByIdDesc();

    // Cuenta cuántos items de una inspección tienen un resultado específico
    long countBySanitaryIdAndResultadoItem(
            Long sanitaryId, String resultadoItem);
}