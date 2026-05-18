// service/EntryItemService.java
// Lógica de negocio para los items declarados en cada ingreso

package com.example.EntryService.service;

import com.example.EntryService.dto.EntryItemDTO;
import com.example.EntryService.model.Entry;
import com.example.EntryService.model.EntryItem;
import com.example.EntryService.repository.EntryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryItemService {


    private final EntryItemRepository itemRepository;

    // EntryService para verificar que el ingreso existe
    private final EntryService entryService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<EntryItem> obtenerTodos() {
        log.info("Obteniendo todos los items de ingreso");
        return itemRepository.findAll();
    }

    public EntryItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Agregar item declarado a un ingreso
    public EntryItem agregar(EntryItemDTO dto) {
        log.info("Agregando item al ingreso id: {}", dto.getEntryId());

        // REGLA DE NEGOCIO 1: el ingreso debe existir
        Entry ingreso = entryService.obtenerPorId(dto.getEntryId());

        // REGLA DE NEGOCIO 2: solo se agregan items a ingresos PENDIENTES
        if (!ingreso.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — ingreso en estado: {}",
                    ingreso.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a ingresos "
                            + "en estado PENDIENTE");
        }

        // Mapeo DTO → Entidad
        EntryItem item = new EntryItem();
        item.setEntry(ingreso);
        item.setDescripcion(dto.getDescripcion());
        item.setCantidad(dto.getCantidad());
        item.setValorUsd(dto.getValorUsd());
        item.setAprobado(false);

        EntryItem guardado = itemRepository.save(item);
        log.info("Item de ingreso agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Aprobar un item declarado
    public EntryItem aprobar(Long id) {
        log.info("Aprobando item con id: {}", id);
        EntryItem item = obtenerPorId(id);
        item.setAprobado(true);
        EntryItem actualizado = itemRepository.save(item);
        log.info("Item {} aprobado correctamente", id);
        return actualizado;
    }

    // Rechazar un item declarado
    public EntryItem rechazar(Long id) {
        log.info("Rechazando item con id: {}", id);
        EntryItem item = obtenerPorId(id);
        item.setAprobado(false);
        EntryItem actualizado = itemRepository.save(item);
        log.info("Item {} rechazado correctamente", id);
        return actualizado;
    }

    // Eliminar item
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

    // -------------------------------------------------------
    // CONSULTAS DERIVADAS
    // -------------------------------------------------------

    public List<EntryItem> obtenerPorIngreso(Long entryId) {
        log.info("Obteniendo items del ingreso: {}", entryId);
        return itemRepository.findByEntryId(entryId);
    }

    public List<EntryItem> obtenerAprobadosPorIngreso(Long entryId) {
        log.info("Obteniendo items aprobados del ingreso: {}", entryId);
        return itemRepository.findByEntryIdAndAprobadoTrue(entryId);
    }

    public List<EntryItem> obtenerNoAprobadosPorIngreso(Long entryId) {
        log.info("Obteniendo items no aprobados del ingreso: {}",
                entryId);
        return itemRepository.findByEntryIdAndAprobadoFalse(entryId);
    }

    public List<EntryItem> buscarPorDescripcion(String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    public List<EntryItem> obtenerPorIngresoOrdenadosPorValor(
            Long entryId) {
        log.info("Obteniendo items del ingreso {} ordenados por valor",
                entryId);
        return itemRepository.findByEntryIdOrderByValorUsdDesc(entryId);
    }

    public List<EntryItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items de ingreso");
        return itemRepository.findTop10ByOrderByIdDesc();
    }
}