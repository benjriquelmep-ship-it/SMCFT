// Lógica de negocio para los items de equipaje de cada cruce
// Se comunica con Item Category Service para validar categorías
package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.dto.BorderCrossingItemDTO;
import com.example.BorderCrossingService.dto.ItemCategoryResponseDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.model.BorderCrossingItem;
import com.example.BorderCrossingService.repository.BorderCrossingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorderCrossingItemService {

<<<<<<< HEAD
    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(BorderCrossingItemService.class);
=======
>>>>>>> 042337db361a22d3143d66d1ebbfca96e8954f65

    // Accede a la tabla border_crossing_items en la BD
    private final BorderCrossingItemRepository itemRepository;

    // Necesario para verificar que el cruce existe antes de agregar un item
    private final BorderCrossingService crossingService;

    // WebClient para llamar a Item Category Service
    @Qualifier("itemCategoryWebClient")
    private final WebClient itemCategoryWebClient;

    // Devuelve todos los items de la BD
    public List<BorderCrossingItem> obtenerTodos() {
        log.info("Obteniendo todos los items de equipaje");
        return itemRepository.findAll();
    }

    // Busca un item por su id. Si no existe lanza RuntimeException → HTTP 404
    public BorderCrossingItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Agrega un nuevo item de equipaje a un cruce existente
    public BorderCrossingItem agregar(BorderCrossingItemDTO dto) {
        log.info("Agregando item al cruce id: {}",
                dto.getBorderCrossingId());

        // REGLA 1: el cruce debe existir en la BD
        // Si no existe → crossingService lanza RuntimeException → HTTP 404
        BorderCrossing cruce =
                crossingService.obtenerPorId(dto.getBorderCrossingId());

        // REGLA 2: solo se agregan items a cruces PENDIENTES
        // Si el cruce ya fue AUTORIZADO o RECHAZADO → no se pueden agregar items
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — cruce en estado: {}",
                    cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a cruces en estado PENDIENTE");
        }

        // REGLA 3: la categoría del item debe existir en Item Category Service
        // Llama al microservicio para verificar que la categoría es válida
        verificarCategoriaEnItemCategoryService(dto.getCategoriaId());

        // Mapeo DTO → Entidad
        // Convierte el formulario que llegó en un objeto para guardar en la BD
        BorderCrossingItem item = new BorderCrossingItem();
        item.setBorderCrossing(cruce);              // FK hacia el cruce
        item.setCategoriaId(dto.getCategoriaId());  // categoría del item
        item.setDescripcion(dto.getDescripcion());  // Ej: "laptop HP", "ropa"
        item.setCantidad(dto.getCantidad());        // cuántas unidades
        item.setValorUsd(dto.getValorUsd());        // valor en dólares
        item.setAprobado(false);                    // inicia como no aprobado

        // Guarda en la BD y retorna el item con su id generado
        BorderCrossingItem guardado = itemRepository.save(item);
        log.info("Item agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Aprueba un item — cambia aprobado a true
    // El fiscalizador decide que ese objeto puede pasar la frontera
    public BorderCrossingItem aprobar(Long id) {
        log.info("Aprobando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(true);
        BorderCrossingItem actualizado = itemRepository.save(item);
        log.info("Item {} aprobado correctamente", id);
        return actualizado;
    }

    // Rechaza un item — cambia aprobado a false
    // El fiscalizador decide que ese objeto NO puede pasar la frontera
    public BorderCrossingItem rechazar(Long id) {
        log.info("Rechazando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(false);
        BorderCrossingItem actualizado = itemRepository.save(item);
        log.info("Item {} rechazado correctamente", id);
        return actualizado;
    }

    // Elimina un item por su id
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

    // Devuelve todos los items que pertenecen a un cruce específico
    public List<BorderCrossingItem> obtenerPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce: {}", borderCrossingId);
        return itemRepository.findByBorderCrossingId(borderCrossingId);
    }

    // Devuelve solo los items APROBADOS de un cruce
    public List<BorderCrossingItem> obtenerAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoTrue(borderCrossingId);
    }

    // Devuelve los items NO APROBADOS de un cruce
    public List<BorderCrossingItem> obtenerNoAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items no aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoFalse(borderCrossingId);
    }

    // Devuelve todos los items de una categoría específica
    public List<BorderCrossingItem> obtenerPorCategoria(Long categoriaId) {
        log.info("Obteniendo items de la categoría: {}", categoriaId);
        return itemRepository.findByCategoriaId(categoriaId);
    }

    // Busca items cuya descripción contenga el texto buscado
    public List<BorderCrossingItem> buscarPorDescripcion(
            String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    // Devuelve items de un cruce ordenados por valor del más caro al más barato
    public List<BorderCrossingItem> obtenerPorCruceOrdenadosPorValor(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce {} ordenados por valor",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdOrderByValorUsdDesc(borderCrossingId);
    }

    // Devuelve los últimos 10 items registrados en el sistema
    public List<BorderCrossingItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items");
        return itemRepository.findTop10ByOrderByIdDesc();
    }

    // Verifica que la categoría existe y está activa en Item Category Service
    // Se llama antes de agregar un item para validar su categoría
    private void verificarCategoriaEnItemCategoryService(Long categoriaId) {
        try {
            log.info("Verificando categoría {} en Item Category Service",
                    categoriaId);
            ItemCategoryResponseDTO categoria = itemCategoryWebClient.get()
                    .uri("/api/v1/item-categories/{id}", categoriaId)
                    .retrieve()
                    .bodyToMono(ItemCategoryResponseDTO.class)
                    .block();

            // REGLA: la categoría debe estar activa
            // Si activo = false → esa categoría fue desactivada
            if (!categoria.getActivo()) {
                log.warn("Categoría {} está inactiva", categoriaId);
                throw new RuntimeException(
                        "La categoría con id " + categoriaId
                                + " está inactiva en el sistema");
            }

            log.info("Categoría {} verificada correctamente", categoriaId);

        } catch (WebClientResponseException.NotFound e) {

            // Item Category Service respondió HTTP 404 → la categoría no existe
            log.warn("Categoría no encontrada: {}", categoriaId);
            throw new RuntimeException(
                    "La categoría con id " + categoriaId
                            + " no existe en el sistema");

        } catch (RuntimeException e) {

            // Re-lanza el RuntimeException sin modificarlo
            // Así los errores de las reglas de negocio llegan al Controller
            throw e;

        } catch (Exception e) {
            // Cualquier otro error — Item Category Service caído o sin conexión
            log.error("Error al comunicarse con Item Category Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar la categoría. "
                            + "Verifique que Item Category Service esté corriendo "
                            + "en el puerto 8088");
        }
    }
}