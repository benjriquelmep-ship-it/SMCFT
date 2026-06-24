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

    private final BorderCrossingItemRepository itemRepository;
    private final BorderCrossingService crossingService;
    @Qualifier("itemCategoryWebClient")
    private final WebClient itemCategoryWebClient;

    public List<BorderCrossingItem> obtenerTodos() {
        log.info("Obteniendo todos los items de equipaje");
        return itemRepository.findAll();
    }

    public BorderCrossingItem obtenerPorId(Long id) {
        log.info("Buscando item con id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Item con id {} no encontrado", id);
                    return new RuntimeException(
                            "Item no encontrado con id: " + id);
                });
    }

    public BorderCrossingItem agregar(BorderCrossingItemDTO dto) {
        log.info("Agregando item al cruce id: {}",
                dto.getBorderCrossingId());

        BorderCrossing cruce =
                crossingService.obtenerPorId(dto.getBorderCrossingId());

        if (!cruce.getEstado().equals("PENDIENTE")) {
            log.warn("No se pueden agregar items — cruce en estado: {}",
                    cruce.getEstado());
            throw new RuntimeException(
                    "Solo se pueden agregar items a cruces en estado PENDIENTE");
        }

        verificarCategoriaEnItemCategoryService(dto.getCategoriaId());

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

    public BorderCrossingItem aprobar(Long id) {
        log.info("Aprobando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(true);
        BorderCrossingItem actualizado = itemRepository.save(item);
        log.info("Item {} aprobado correctamente", id);
        return actualizado;
    }

    public BorderCrossingItem rechazar(Long id) {
        log.info("Rechazando item con id: {}", id);
        BorderCrossingItem item = obtenerPorId(id);
        item.setAprobado(false);
        BorderCrossingItem actualizado = itemRepository.save(item);
        log.info("Item {} rechazado correctamente", id);
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

    public List<BorderCrossingItem> obtenerPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce: {}", borderCrossingId);
        return itemRepository.findByBorderCrossingId(borderCrossingId);
    }

    public List<BorderCrossingItem> obtenerAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoTrue(borderCrossingId);
    }

    public List<BorderCrossingItem> obtenerNoAprobadosPorCruce(
            Long borderCrossingId) {
        log.info("Obteniendo items no aprobados del cruce: {}",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdAndAprobadoFalse(borderCrossingId);
    }

    public List<BorderCrossingItem> obtenerPorCategoria(Long categoriaId) {
        log.info("Obteniendo items de la categoría: {}", categoriaId);
        return itemRepository.findByCategoriaId(categoriaId);
    }

    public List<BorderCrossingItem> buscarPorDescripcion(
            String descripcion) {
        log.info("Buscando items con descripción: {}", descripcion);
        return itemRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    public List<BorderCrossingItem> obtenerPorCruceOrdenadosPorValor(
            Long borderCrossingId) {
        log.info("Obteniendo items del cruce {} ordenados por valor",
                borderCrossingId);
        return itemRepository
                .findByBorderCrossingIdOrderByValorUsdDesc(borderCrossingId);
    }

    public List<BorderCrossingItem> obtenerUltimosItems() {
        log.info("Obteniendo los últimos 10 items");
        return itemRepository.findTop10ByOrderByIdDesc();
    }

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