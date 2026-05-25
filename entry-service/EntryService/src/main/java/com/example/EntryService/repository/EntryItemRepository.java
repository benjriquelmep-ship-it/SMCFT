// Accede a la tabla entry_items en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método
package com.example.EntryService.repository;

import com.example.EntryService.model.EntryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntryItemRepository
        extends JpaRepository<EntryItem, Long> {

    // Devuelve todos los items que pertenecen a un ingreso específico
    List<EntryItem> findByEntryId(Long entryId);

    // Devuelve solo los items APROBADOS de un ingreso específico
    List<EntryItem> findByEntryIdAndAprobadoTrue(Long entryId);

    // Devuelve solo los items NO APROBADOS de un ingreso específico
    List<EntryItem> findByEntryIdAndAprobadoFalse(Long entryId);

    // Devuelve TODOS los items aprobados del sistema
    List<EntryItem> findByAprobadoTrue();

    // Devuelve TODOS los items no aprobados del sistema
    List<EntryItem> findByAprobadoFalse();

    // Busca items cuya descripción contenga el texto buscado
    List<EntryItem> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // Devuelve los items de un ingreso ordenados por valor en USD del más caro al más barato
    // Útil para identificar los objetos más costosos de un ingreso
    List<EntryItem> findByEntryIdOrderByValorUsdDesc(Long entryId);

    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear en tiempo real los últimos objetos declarados
    List<EntryItem> findTop10ByOrderByIdDesc();
}