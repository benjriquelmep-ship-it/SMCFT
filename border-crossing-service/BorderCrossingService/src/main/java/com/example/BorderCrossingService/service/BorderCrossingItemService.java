// service/BorderCrossingItemService.java
// Lógica de negocio para los items de equipaje
// Se comunica con Item Category Service para validar categorías

package com.example.BorderCrossingService.service;

import com.example.BorderCrossingService.dto.BorderCrossingItemDTO;
import com.example.BorderCrossingService.dto.ItemCategoryResponseDTO;
import com.example.BorderCrossingService.model.BorderCrossing;
import com.example.BorderCrossingService.model.BorderCrossingItem;
import com.example.BorderCrossingService.repository.BorderCrossingItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorderCrossingItemService {

    private static final Logger log =
            LoggerFactory.getLogger(BorderCrossingItemService.class);

    private final BorderCrossingItemRepository itemRepository;

    // BorderCrossingService para verificar que el cruce existe
    private final BorderCrossingService crossingService;

    // WebClient para Item Category Service
    @Qualifier("itemCategoryWebClient")
    private final WebClient itemCategoryWebClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Obtener todos los items
    public List<BorderCrossingItem> obtenerTodos() {
        log.info("Obteniendo todos los items de equipaje");
        return itemRepository.findAll();
    }

    // Obtener item por id
    public BorderCrossingItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    // Agregar item de equipaje a un cruce
    public BorderCrossingItem agregar(BorderCrossingItemDTO dto) {
        log.info("Agregando item al cruce id: {}",
                dto.getBorderCrossingId());

        // REGLA DE NEGOCIO 1: el cruce debe existir
        BorderCrossing cruce =
                crossingService.obtenerPorId(dto.getBorderCrossingId());

        // REGLA DE NEGOCIO 2: solo se agregan items a cruces PENDIENTES
        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — cruce en estado: {}",
                    cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a cruces en estado PENDIENTE");
        }

        // REGLA DE NEGOCIO 3: verificar que la categoría existe
        // Comunicación con Item Category Service
        verificarCategoriaEnItemCategoryService(dto.getCategoriaId());

        // Mapeo DTO → Entidad
        BorderCrossingItem item = new BorderCrossingItem();
        item.setBorderCrossing(cruce);
        item.setCategoriaId(dto.getCategoriaId());
        item.setDescripcion(dto.getDescripcion());
        item.setCantidad(dto.getCantidad());
        item.setValorUsd(dto.getValorUsd());
        item.setAprobado(false);

        BorderCrossingItem guardado = itemRepository.save(item);
        log.info("Item agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Aprobar un item de equipaje
    public BorderCrossingItem aprobar(Long id) {
        log.info("Aprobando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(true);
        BorderCrossingItem actualizado = itemRepository.save(item);
        log.info("Item {} aprobado correctamente", id);
        return actualizado;
    }

    // Rechazar un item de equipaje
    public BorderCrossingItem rechazar(Long id) {
        log.info("Rechazando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(false);
        BorderCrossingItem actualizado = itemRepository.save(item);
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

    // Todos los items de un cruce
    public List<BorderCrossingItem> obtenerPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce: {}", borderCrossingId);
        return itemRepository.findByBorderCrossingId(borderCrossingId);
    }

    // Items aprobados de un cruce
    public List<BorderCrossingItem> obtenerAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoTrue(borderCrossingId);
    }

    // Items no aprobados de un cruce
    public List<BorderCrossingItem> obtenerNoAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items no aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoFalse(borderCrossingId);
    }

    // Items por categoría
    public List<BorderCrossingItem> obtenerPorCategoria(Long categoriaId) {
        log.info("Obteniendo items de la categoría: {}", categoriaId);
        return itemRepository.findByCategoriaId(categoriaId);
    }

    // Búsqueda parcial por descripción
    public List<BorderCrossingItem> buscarPorDescripcion(
            String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    // Items de un cruce ordenados de mayor a menor valor
    public List<BorderCrossingItem> obtenerPorCruceOrdenadosPorValor(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce {} ordenados por valor",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdOrderByValorUsdDesc(borderCrossingId);
    }

    // Los últimos 10 items registrados
    public List<BorderCrossingItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items");
        return itemRepository.findTop10ByOrderByIdDesc();
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON ITEM CATEGORY SERVICE — WebClient
    // -------------------------------------------------------

    // Verifica que la categoría existe y está activa en Item Category Service
    private void verificarCategoriaEnItemCategoryService(Long categoriaId) {
        try {
            log.info("Verificando categoría {} en Item Category Service",
                    categoriaId);

            ItemCategoryResponseDTO categoria = itemCategoryWebClient.get()
                    // GET http://localhost:8088/api/v1/item-categories/1
                    .uri("/api/v1/item-categories/{id}", categoriaId)
                    .retrieve()
                    .bodyToMono(ItemCategoryResponseDTO.class)
                    .block();

            // REGLA DE NEGOCIO: la categoría debe estar activa
            if (!categoria.getActivo()) {
                log.warn("Categoría {} está inactiva", categoriaId);
                throw new RuntimeException(
                        "La categoría con id " + categoriaId
                                + " está inactiva en el sistema");
            }

            log.info("Categoría {} verificada correctamente", categoriaId);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Categoría no encontrada: {}", categoriaId);
            throw new RuntimeException(
                    "La categoría con id " + categoriaId
                            + " no existe en el sistema");

        } catch (RuntimeException e) {
            // Re-lanzar excepciones de negocio sin envolverlas
            throw e;

        } catch (Exception e) {
            log.error("Error al comunicarse con Item Category Service: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al verificar la categoría. "
                            + "Verifique que Item Category Service esté corriendo "
                            + "en el puerto 8088");
        }
    }
}