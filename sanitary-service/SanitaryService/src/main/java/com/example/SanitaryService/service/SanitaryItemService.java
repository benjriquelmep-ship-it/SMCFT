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

    private final SanitaryItemRepository itemRepository;
    private final SanitaryService sanitaryService;

    public List<SanitaryItem> obtenerTodos() {
        log.info("Obteniendo todos los items sanitarios");
        return itemRepository.findAll();
    }

    public SanitaryItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

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

        SanitaryItem item = new SanitaryItem();
        item.setSanitary(inspeccion);
        item.setDescripcion(dto.getDescripcion());
        item.setResultadoItem(dto.getResultadoItem());
        item.setObservaciones(dto.getObservaciones());
        SanitaryItem guardado = itemRepository.save(item);
        log.info("Item sanitario agregado con id: {}", guardado.getId());
        return guardado;
    }

    public SanitaryItem actualizar(Long id, SanitaryItemDTO dto) {
        log.info("Actualizando item con id: {}", id);

        SanitaryItem existente = obtenerPorId(id);
        existente.setDescripcion(dto.getDescripcion());
        existente.setResultadoItem(dto.getResultadoItem());
        existente.setObservaciones(dto.getObservaciones());

        SanitaryItem actualizado = itemRepository.save(existente);
        log.info("Item {} actualizado correctamente", id);
        return actualizado;
    }

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


    public List<SanitaryItem> obtenerPorInspeccion(Long sanitaryId) {
        log.info("Obteniendo items de la inspección: {}", sanitaryId);
        return itemRepository.findBySanitaryId(sanitaryId);
    }

    public List<SanitaryItem> obtenerPorInspeccionYResultado(
            Long sanitaryId, String resultadoItem) {
        log.info("Obteniendo items de {} con resultado {}",
                sanitaryId, resultadoItem);
        return itemRepository.findBySanitaryIdAndResultadoItem(
                sanitaryId, resultadoItem);
    }

    public List<SanitaryItem> obtenerPorResultado(String resultadoItem) {
        log.info("Obteniendo items con resultado: {}", resultadoItem);
        return itemRepository.findByResultadoItem(resultadoItem);
    }

    public List<SanitaryItem> buscarPorDescripcion(String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    public List<SanitaryItem> obtenerPorInspeccionOrdenados(
            Long sanitaryId) {
        log.info("Obteniendo items de {} ordenados", sanitaryId);
        return itemRepository
                .findBySanitaryIdOrderByDescripcionAsc(sanitaryId);
    }

    public List<SanitaryItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items sanitarios");
        return itemRepository.findTop10ByOrderByIdDesc();
    }

    public long contarRechazadosPorInspeccion(Long sanitaryId) {
        log.info("Contando items rechazados de inspección: {}",
                sanitaryId);
        return itemRepository.countBySanitaryIdAndResultadoItem(
                sanitaryId, "RECHAZADO");
    }
}