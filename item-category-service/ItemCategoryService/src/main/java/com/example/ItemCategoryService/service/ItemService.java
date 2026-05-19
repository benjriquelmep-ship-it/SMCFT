// service/ItemService.java
// Lógica de negocio para los items de cada categoría

package com.example.ItemCategoryService.service;

import com.example.ItemCategoryService.dto.ItemDTO;
import com.example.ItemCategoryService.model.Item;
import com.example.ItemCategoryService.model.ItemCategory;
import com.example.ItemCategoryService.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {


    private final ItemRepository itemRepository;

    // ItemCategoryService para verificar que la categoría existe
    private final ItemCategoryService categoryService;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    public List<Item> obtenerTodos() {
        log.info("Obteniendo todos los items");
        return itemRepository.findAll();
    }

    public Item obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    public Item crear(ItemDTO dto) {
        log.info("Creando item: {}", dto.getNombre());

        // REGLA DE NEGOCIO: la categoría debe existir y estar activa
        ItemCategory categoria =
                categoryService.obtenerPorId(dto.getCategoriaId());

        if (!categoria.getActivo()) {
            log.warn("Categoría {} está inactiva", dto.getCategoriaId());
            throw new RuntimeException(
                    "No se pueden agregar items a una categoría inactiva");
        }

        // Mapeo DTO → Entidad
        Item nuevo = new Item();
        nuevo.setCategory(categoria);
        nuevo.setNombre(dto.getNombre());
        nuevo.setDescripcion(dto.getDescripcion());
        nuevo.setUnidad(dto.getUnidad());
        nuevo.setActivo(true);

        Item guardado = itemRepository.save(nuevo);
        log.info("Item creado con id: {}", guardado.getId());
        return guardado;
    }

    public Item actualizar(Long id, ItemDTO dto) {
        log.info("Actualizando item con id: {}", id);
        Item existente = obtenerPorId(id);

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUnidad(dto.getUnidad());

        Item actualizado = itemRepository.save(existente);
        log.info("Item {} actualizado correctamente", id);
        return actualizado;
    }

    // Desactivar item — soft delete
    public void desactivar(Long id) {
        log.info("Desactivando item con id: {}", id);
        Item existente = obtenerPorId(id);
        existente.setActivo(false);
        itemRepository.save(existente);
        log.info("Item {} desactivado correctamente", id);
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

    public List<Item> obtenerPorCategoria(Long categoryId) {
        log.info("Obteniendo items de la categoría: {}", categoryId);
        return itemRepository.findByCategoryId(categoryId);
    }

    public List<Item> obtenerActivosPorCategoria(Long categoryId) {
        log.info("Obteniendo items activos de la categoría: {}",
                categoryId);
        return itemRepository.findByCategoryIdAndActivoTrue(categoryId);
    }

    public List<Item> obtenerActivos() {
        log.info("Obteniendo todos los items activos");
        return itemRepository.findByActivoTrue();
    }

    public List<Item> buscarPorNombre(String texto) {
        log.info("Buscando items con nombre: {}", texto);
        return itemRepository.findByNombreContainingIgnoreCase(texto);
    }

    public List<Item> obtenerPorUnidad(String unidad) {
        log.info("Obteniendo items con unidad: {}", unidad);
        return itemRepository.findByUnidad(unidad);
    }

    public List<Item> obtenerPorCategoriaOrdenados(Long categoryId) {
        log.info("Obteniendo items de {} ordenados por nombre",
                categoryId);
        return itemRepository
                .findByCategoryIdOrderByNombreAsc(categoryId);
    }

    public List<Item> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items");
        return itemRepository.findTop10ByOrderByIdDesc();
    }
}