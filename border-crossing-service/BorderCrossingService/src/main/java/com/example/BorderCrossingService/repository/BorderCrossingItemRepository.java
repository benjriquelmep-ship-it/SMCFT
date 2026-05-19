// Accede a la tabla border_crossing_items en la base de datos
// Spring genera el SQL automáticamente leyendo el nombre de cada método

package com.example.BorderCrossingService.repository;

import com.example.BorderCrossingService.model.BorderCrossingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorderCrossingItemRepository
        extends JpaRepository<BorderCrossingItem, Long> {

    // Devuelve todos los items que pertenecen a un cruce específico
    List<BorderCrossingItem> findByBorderCrossingId(Long borderCrossingId);

    // Devuelve solo los items APROBADOS de un cruce específico
    List<BorderCrossingItem> findByBorderCrossingIdAndAprobadoTrue(
            Long borderCrossingId);

    // Devuelve solo los items NO APROBADOS de un cruce específico
    List<BorderCrossingItem> findByBorderCrossingIdAndAprobadoFalse(
            Long borderCrossingId);

    // Devuelve todos los items de una categoría específica
    List<BorderCrossingItem> findByCategoriaId(Long categoriaId);

    // Devuelve TODOS los items aprobados del sistema
    List<BorderCrossingItem> findByAprobadoTrue();

    // Devuelve TODOS los items no aprobados del sistema
    List<BorderCrossingItem> findByAprobadoFalse();

    // Busca items cuya descripción contenga el texto buscado
    List<BorderCrossingItem> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // Devuelve los items de un cruce ordenados por valor en USD del más caro al más barato
    // Útil para identificar los objetos más costosos de un cruce
    List<BorderCrossingItem> findByBorderCrossingIdOrderByValorUsdDesc(
            Long borderCrossingId);

    // Devuelve los últimos 10 items registrados en el sistema
    // Útil para monitorear en tiempo real los últimos objetos declarados
    List<BorderCrossingItem> findTop10ByOrderByIdDesc();
}