// Lógica de negocio para los items de cada categoría
// Un item = objeto específico dentro de una categoría
// Ej: categoría "Electrónica" → items: "Laptop", "Celular", "Tablet"
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

    // Accede a la tabla items en la BD
    private final ItemRepository itemRepository;

    // Necesario para verificar que la categoría existe y está activa
    // antes de crear un item
    private final ItemCategoryService categoryService;

    // Devuelve todos los items de la BD
    // Incluye activos e inactivos de todas las categorías
    public List<Item> obtenerTodos() {
        log.info("Obteniendo todos los items");
        return itemRepository.findAll();
    }

    // Busca un item por su id
    // Si no existe lanza RuntimeException → HTTP 404
    public Item obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Crea un nuevo item dentro de una categoría
    public Item crear(ItemDTO dto) {
        log.info("Creando item: {}", dto.getNombre());

        // REGLA 1: la categoría debe existir en la BD
        // Si no existe → categoryService lanza RuntimeException → HTTP 404
        ItemCategory categoria =
                categoryService.obtenerPorId(dto.getCategoriaId());

        // REGLA 2: la categoría debe estar activa
        // No se pueden agregar items a categorías desactivadas
        // Ej: si "Electrónica" fue desactivada no se pueden crear más items
        if (!categoria.getActivo()) {
            log.warn("Categoría {} está inactiva", dto.getCategoriaId());
            throw new RuntimeException(
                    "No se pueden agregar items a una categoría inactiva");
        }

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        Item nuevo = new Item();
        nuevo.setCategory(categoria);              // FK hacia la categoría
        nuevo.setNombre(dto.getNombre());          // Ej: "Laptop HP"
        nuevo.setDescripcion(dto.getDescripcion()); // descripción del item
        nuevo.setUnidad(dto.getUnidad());          // Ej: "und", "kg", "lt"
        nuevo.setActivo(true);                     // todo item nuevo inicia activo

        // Guarda en la BD y retorna el item con su id generado
        Item guardado = itemRepository.save(nuevo);
        log.info("Item creado con id: {}", guardado.getId());
        return guardado;
    }

    // Actualiza los campos de un item existente
    // Se usa con PUT — reemplaza nombre, descripción y unidad
    // No cambia la categoría ni el estado activo
    public Item actualizar(Long id, ItemDTO dto) {
        log.info("Actualizando item con id: {}", id);

        // Busca el item existente — si no existe → HTTP 404
        Item existente = obtenerPorId(id);

        // Solo actualiza estos 3 campos — no cambia la categoría ni activo
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUnidad(dto.getUnidad());

        Item actualizado = itemRepository.save(existente);
        log.info("Item {} actualizado correctamente", id);
        return actualizado;
    }

    // Desactiva un item — cambia activo a false
    // Soft delete = no borra de la BD, solo lo marca como inactivo
    // Los items desactivados no pueden usarse en nuevos cruces
    public void desactivar(Long id) {
        log.info("Desactivando item con id: {}", id);
        Item existente = obtenerPorId(id);
        // Cambia activo de true a false
        existente.setActivo(false);
        itemRepository.save(existente);
        log.info("Item {} desactivado correctamente", id);
    }

    // Elimina un item por su id — eliminación permanente de la BD
    // A diferencia de desactivar() este método SÍ borra el registro
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

    // Devuelve todos los items de una categoría (activos e inactivos)
    public List<Item> obtenerPorCategoria(Long categoryId) {
        log.info("Obteniendo items de la categoría: {}", categoryId);
        return itemRepository.findByCategoryId(categoryId);
    }

    // Devuelve solo los items ACTIVOS de una categoría
    // Los items activos son los que se pueden usar en nuevos cruces
    public List<Item> obtenerActivosPorCategoria(Long categoryId) {
        log.info("Obteniendo items activos de la categoría: {}",
                categoryId);
        return itemRepository.findByCategoryIdAndActivoTrue(categoryId);
    }

    // Devuelve TODOS los items activos del sistema
    public List<Item> obtenerActivos() {
        log.info("Obteniendo todos los items activos");
        return itemRepository.findByActivoTrue();
    }

    // Busca items cuyo nombre contenga el texto buscado
    public List<Item> buscarPorNombre(String texto) {
        log.info("Buscando items con nombre: {}", texto);
        return itemRepository.findByNombreContainingIgnoreCase(texto);
    }

    // Devuelve todos los items de una unidad de medida específica
    public List<Item> obtenerPorUnidad(String unidad) {
        log.info("Obteniendo items con unidad: {}", unidad);
        return itemRepository.findByUnidad(unidad);
    }

    // Devuelve items de una categoría ordenados alfabéticamente por nombre
    // Útil para mostrar una lista ordenada en el frontend
    public List<Item> obtenerPorCategoriaOrdenados(Long categoryId) {
        log.info("Obteniendo items de {} ordenados por nombre",
                categoryId);
        return itemRepository
                .findByCategoryIdOrderByNombreAsc(categoryId);
    }

    // Devuelve los últimos 10 items registrados en el sistema
    public List<Item> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items");
        return itemRepository.findTop10ByOrderByIdDesc();
    }
}