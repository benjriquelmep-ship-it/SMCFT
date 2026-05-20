// Lógica de negocio para los items declarados en cada ingreso al país
// Un item = objeto o mercancía que el conductor declara al ingresar
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

    // Accede a la tabla entry_items en la BD
    private final EntryItemRepository itemRepository;

    // Necesario para verificar que el ingreso existe
    // antes de agregar un item
    private final EntryService entryService;

    // Devuelve todos los items de la BD
    public List<EntryItem> obtenerTodos() {
        log.info("Obteniendo todos los items de ingreso");
        return itemRepository.findAll();
    }

    // Busca un item por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public EntryItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Agrega un nuevo item declarado a un ingreso existente
    public EntryItem agregar(EntryItemDTO dto) {
        log.info("Agregando item al ingreso id: {}", dto.getEntryId());

        // REGLA 1: el ingreso debe existir en la BD
        // Si no existe → entryService lanza RuntimeException → HTTP 404
        Entry ingreso = entryService.obtenerPorId(dto.getEntryId());

        // REGLA 2: solo se agregan items a ingresos PENDIENTES
        // Si el ingreso ya fue AUTORIZADO o RECHAZADO
        // no se pueden agregar más items
        if (!ingreso.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — ingreso en estado: {}",
                    ingreso.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a ingresos "
                            + "en estado PENDIENTE");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        EntryItem item = new EntryItem();
        item.setEntry(ingreso);                    // FK hacia el ingreso
        item.setDescripcion(dto.getDescripcion()); // Ej: "laptop HP", "ropa"
        item.setCantidad(dto.getCantidad());       // cuántas unidades
        item.setValorUsd(dto.getValorUsd());       // valor en dólares
        item.setAprobado(false);                   // inicia como no aprobado

        // Guarda en la BD y retorna el item con su id generado
        EntryItem guardado = itemRepository.save(item);
        log.info("Item de ingreso agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Aprueba un item — el fiscalizador acepta que ese objeto ingrese
    // Cambia aprobado de false a true
    public EntryItem aprobar(Long id) {
        log.info("Aprobando item con id: {}", id);
        EntryItem item = obtenerPorId(id);
        item.setAprobado(true);
        EntryItem actualizado = itemRepository.save(item);
        log.info("Item {} aprobado correctamente", id);
        return actualizado;
    }

    // Rechaza un item — el fiscalizador no acepta que ese objeto ingrese
    // Cambia aprobado de true a false
    public EntryItem rechazar(Long id) {
        log.info("Rechazando item con id: {}", id);
        EntryItem item = obtenerPorId(id);
        item.setAprobado(false);
        EntryItem actualizado = itemRepository.save(item);
        log.info("Item {} rechazado correctamente", id);
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

    // Devuelve todos los items que pertenecen a un ingreso específico
    public List<EntryItem> obtenerPorIngreso(Long entryId) {
        log.info("Obteniendo items del ingreso: {}", entryId);
        return itemRepository.findByEntryId(entryId);
    }

    // Devuelve solo los items APROBADOS de un ingreso específico
    public List<EntryItem> obtenerAprobadosPorIngreso(Long entryId) {
        log.info("Obteniendo items aprobados del ingreso: {}", entryId);
        return itemRepository.findByEntryIdAndAprobadoTrue(entryId);
    }

    // Devuelve los items NO APROBADOS de un ingreso específico
    public List<EntryItem> obtenerNoAprobadosPorIngreso(Long entryId) {
        log.info("Obteniendo items no aprobados del ingreso: {}",
                entryId);
        return itemRepository.findByEntryIdAndAprobadoFalse(entryId);
    }

    // Busca items cuya descripción contenga el texto buscado
    public List<EntryItem> buscarPorDescripcion(String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    // Devuelve items de un ingreso ordenados por valor del más caro al más barato
    public List<EntryItem> obtenerPorIngresoOrdenadosPorValor(
            Long entryId) {
        log.info("Obteniendo items del ingreso {} ordenados por valor",
                entryId);
        return itemRepository.findByEntryIdOrderByValorUsdDesc(entryId);
    }

    // Devuelve los últimos 10 items registrados en el sistema
    public List<EntryItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items de ingreso");
        return itemRepository.findTop10ByOrderByIdDesc();
    }
}