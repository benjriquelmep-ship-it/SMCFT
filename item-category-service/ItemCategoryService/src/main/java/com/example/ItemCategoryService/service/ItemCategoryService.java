// Lógica de negocio del Item Category Service
// Gestiona las categorías de items que se pueden declarar en los cruces
package com.example.ItemCategoryService.service;

import com.example.ItemCategoryService.dto.ItemCategoryDTO;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.repository.ItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemCategoryService {

    // Accede a la tabla item_categories en la BD
    private final ItemCategoryRepository categoryRepository;

    // Devuelve todas las categorías de la BD
    // Incluye activas e inactivas
    public List<ItemCategory> obtenerTodas() {
        log.info("Obteniendo todas las categorías");
        return categoryRepository.findAll();
    }

    // Busca una categoría por su id
    // Border Crossing Service llama a este método para verificar categorías
    // Si no existe lanza RuntimeException → HTTP 404
    public ItemCategory obtenerPorId(Long id) {
        log.info("Buscando categoría con id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría con id {} no encontrada", id);
                    return new RuntimeException(
                            "Categoría no encontrada con id: " + id);
                });
    }

    // Crea una nueva categoría de items
    public ItemCategory crear(ItemCategoryDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());

        // REGLA: no pueden existir dos categorías con el mismo nombre
        if (categoryRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre de categoría duplicado: {}", dto.getNombre());
            throw new RuntimeException(
                    "Ya existe una categoría con el nombre: "
                            + dto.getNombre());
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        ItemCategory nueva = new ItemCategory();
        nueva.setNombre(dto.getNombre());               // Ej: "Electrónica"
        nueva.setDescripcion(dto.getDescripcion());     // descripción de la categoría
        nueva.setRequiereDeclaracion(dto.getRequiereDeclaracion()); // true o false
        nueva.setLimiteValorUsd(dto.getLimiteValorUsd()); // límite en dólares o null
        nueva.setActivo(true);                          // toda categoría nueva inicia activa

        // Guarda en la BD y retorna la categoría con su id generado
        ItemCategory guardada = categoryRepository.save(nueva);
        log.info("Categoría creada con id: {}", guardada.getId());
        return guardada;
    }

    // Actualiza todos los campos de una categoría existente
    public ItemCategory actualizar(Long id, ItemCategoryDTO dto) {
        log.info("Actualizando categoría con id: {}", id);

        // Busca la categoría existente — si no existe → HTTP 404
        ItemCategory existente = obtenerPorId(id);

        // Reemplaza todos los campos con los nuevos valores
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setRequiereDeclaracion(dto.getRequiereDeclaracion());
        existente.setLimiteValorUsd(dto.getLimiteValorUsd());
        // No se cambia activo aquí — para eso está desactivar()

        ItemCategory actualizada = categoryRepository.save(existente);
        log.info("Categoría {} actualizada correctamente", id);
        return actualizada;
    }

    // Desactiva una categoría — cambia activo a false
    // Soft delete = no borra de la BD, solo la marca como inactiva
    // Las categorías inactivas no pueden usarse en nuevos cruces
    public void desactivar(Long id) {
        log.info("Desactivando categoría con id: {}", id);
        ItemCategory existente = obtenerPorId(id);
        // Cambia activo de true a false — la categoría queda desactivada
        existente.setActivo(false);
        categoryRepository.save(existente);
        log.info("Categoría {} desactivada correctamente", id);
    }

    // Elimina una categoría por su id — eliminación permanente de la BD
    // A diferencia de desactivar() este método SÍ borra el registro
    public void eliminar(Long id) {
        log.info("Eliminando categoría con id: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Categoría con id {} no encontrada", id);
            throw new RuntimeException(
                    "Categoría no encontrada con id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Categoría {} eliminada correctamente", id);
    }

    // Devuelve solo las categorías activas (activo = true)
    public List<ItemCategory> obtenerActivas() {
        log.info("Obteniendo categorías activas");
        return categoryRepository.findByActivoTrue();
    }

    // Devuelve solo las categorías inactivas (activo = false)
    public List<ItemCategory> obtenerInactivas() {
        log.info("Obteniendo categorías inactivas");
        return categoryRepository.findByActivoFalse();
    }
    // Devuelve categorías ACTIVAS que requieren declaración obligatoria
    public List<ItemCategory> obtenerRequierenDeclaracion() {
        log.info("Obteniendo categorías que requieren declaración");
        return categoryRepository
                .findByRequiereDeclaracionTrueAndActivoTrue();
    }

    // Devuelve categorías ACTIVAS que NO requieren declaración especial
    public List<ItemCategory> obtenerSinDeclaracion() {
        log.info("Obteniendo categorías sin declaración especial");
        return categoryRepository
                .findByRequiereDeclaracionFalseAndActivoTrue();
    }

    // Busca categorías cuyo nombre contenga el texto buscado
    public List<ItemCategory> buscarPorNombre(String texto) {
        log.info("Buscando categorías con nombre: {}", texto);
        return categoryRepository
                .findByNombreContainingIgnoreCase(texto);
    }

    // Devuelve categorías cuyo límite de valor sea menor o igual al valor buscado
    public List<ItemCategory> obtenerPorLimiteValor(BigDecimal limite) {
        log.info("Obteniendo categorías con límite <= {}", limite);
        return categoryRepository
                .findByLimiteValorUsdLessThanEqual(limite);
    }

    // Devuelve categorías activas ordenadas alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    public List<ItemCategory> obtenerActivasOrdenadas() {
        log.info("Obteniendo categorías activas ordenadas por nombre");
        return categoryRepository.findByActivoTrueOrderByNombreAsc();
    }
}