// Lógica de negocio para los items de inspecciones sanitarias
// Un item = objeto específico que el inspector del SAG revisó
// Ej: "Caja de manzanas", "Planta con tierra", "Animal vivo"
package com.example.SanitaryService.service;

import com.example.SanitaryService.dto.SanitaryItemDTO;
import com.example.SanitaryService.model.Sanitary;
import com.example.SanitaryService.model.SanitaryItem;
import com.example.SanitaryService.repository.SanitaryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SanitaryItemService {

    // Accede a la tabla sanitary_items en la BD
    private final SanitaryItemRepository itemRepository;

    // Necesario para verificar que la inspección existe
    // y que está PENDIENTE antes de agregar un item
    private final SanitaryService sanitaryService;

    // Devuelve todos los items de la BD
    public List<SanitaryItem> obtenerTodos() {
        log.info("Obteniendo todos los items sanitarios");
        return itemRepository.findAll();
    }

    // Busca un item por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public SanitaryItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Agrega un nuevo item a una inspección sanitaria existente
    public SanitaryItem agregar(SanitaryItemDTO dto) {
        log.info("Agregando item a inspección: {}",
                dto.getInspectionId());

        // REGLA 1: la inspección debe existir en la BD
        // Si no existe → sanitaryService lanza RuntimeException → HTTP 404
        Sanitary inspeccion =
                sanitaryService.obtenerPorId(dto.getInspectionId());

        // REGLA 2: solo se agregan items a inspecciones PENDIENTES
        // Si ya fue APROBADA o RECHAZADA → no se pueden agregar más items
        if (!inspeccion.getResultado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — resultado: {}",
                    inspeccion.getResultado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a inspecciones PENDIENTES");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        SanitaryItem item = new SanitaryItem();
        item.setSanitary(inspeccion);                    // FK hacia la inspección
        item.setDescripcion(dto.getDescripcion());       // Ej: "Caja de manzanas"
        item.setResultadoItem(dto.getResultadoItem());   // APROBADO, RECHAZADO, etc.
        item.setObservaciones(dto.getObservaciones());   // comentarios opcionales

        // Guarda en la BD y retorna el item con su id generado
        SanitaryItem guardado = itemRepository.save(item);
        log.info("Item sanitario agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Actualiza los campos de un item existente
    // Se usa con PUT — reemplaza descripción, resultado y observaciones
    // No cambia la inspección a la que pertenece
    public SanitaryItem actualizar(Long id, SanitaryItemDTO dto) {
        log.info("Actualizando item con id: {}", id);

        // Busca el item existente — si no existe → HTTP 404
        SanitaryItem existente = obtenerPorId(id);

        // Actualiza los 3 campos editables del item
        existente.setDescripcion(dto.getDescripcion());
        existente.setResultadoItem(dto.getResultadoItem());
        existente.setObservaciones(dto.getObservaciones());

        SanitaryItem actualizado = itemRepository.save(existente);
        log.info("Item {} actualizado correctamente", id);
        return actualizado;
    }

    // Elimina un item por su id
    // existsById verifica si existe antes de intentar eliminar
    public void eliminar(Long id) {
        log.info("Eliminando item con id: {}", id);
        if (!itemRepository.existsById(id)) {
            log.warn("Item con id {} no encontrado", id);
            throw new RuntimeException(
                    "Item no encontrado con id: " + id);
        }
        itemRepository.deleteById(id);
        log.info("Item {} eliminado correctamente", id);
    }


    // Devuelve todos los items que pertenecen a una inspección específica
    public List<SanitaryItem> obtenerPorInspeccion(Long sanitaryId) {
        log.info("Obteniendo items de la inspección: {}", sanitaryId);
        return itemRepository.findBySanitaryId(sanitaryId);
    }

    // Devuelve items de una inspección con un resultado específico
    public List<SanitaryItem> obtenerPorInspeccionYResultado(
            Long sanitaryId, String resultadoItem) {
        log.info("Obteniendo items de {} con resultado {}",
                sanitaryId, resultadoItem);
        return itemRepository.findBySanitaryIdAndResultadoItem(
                sanitaryId, resultadoItem);
    }

    // Devuelve TODOS los items del sistema con un resultado específico
    public List<SanitaryItem> obtenerPorResultado(String resultadoItem) {
        log.info("Obteniendo items con resultado: {}", resultadoItem);
        return itemRepository.findByResultadoItem(resultadoItem);
    }

    // Busca items cuya descripción contenga el texto buscado
    public List<SanitaryItem> buscarPorDescripcion(String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    // Devuelve items de una inspección ordenados alfabéticamente
    // Útil para ver los objetos inspeccionados en orden
    public List<SanitaryItem> obtenerPorInspeccionOrdenados(
            Long sanitaryId) {
        log.info("Obteniendo items de {} ordenados", sanitaryId);
        return itemRepository
                .findBySanitaryIdOrderByDescripcionAsc(sanitaryId);
    }

    // Devuelve los últimos 10 items registrados en el sistema
    public List<SanitaryItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items sanitarios");
        return itemRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántos items fueron RECHAZADOS en una inspección específica
    public long contarRechazadosPorInspeccion(Long sanitaryId) {
        log.info("Contando items rechazados de inspección: {}",
                sanitaryId);
        return itemRepository.countBySanitaryIdAndResultadoItem(
                sanitaryId, "RECHAZADO");
    }
}