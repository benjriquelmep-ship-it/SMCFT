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

    private final ItemCategoryRepository categoryRepository;

    public List<ItemCategory> obtenerTodas() {
        log.info("Obteniendo todas las categorías");
        return categoryRepository.findAll();
    }

    public ItemCategory obtenerPorId(Long id) {
        log.info("Buscando categoría con id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría con id {} no encontrada", id);
                    return new RuntimeException(
                            "Categoría no encontrada con id: " + id);
                });
    }

    public ItemCategory crear(ItemCategoryDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());

        // REGLA: no pueden existir dos categorías con el mismo nombre
        if (categoryRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre de categoría duplicado: {}", dto.getNombre());
            throw new RuntimeException(
                    "Ya existe una categoría con el nombre: "
                            + dto.getNombre());
        }

        ItemCategory nueva = new ItemCategory();
        nueva.setNombre(dto.getNombre());
        nueva.setDescripcion(dto.getDescripcion());
        nueva.setRequiereDeclaracion(dto.getRequiereDeclaracion());
        nueva.setLimiteValorUsd(dto.getLimiteValorUsd());
        nueva.setActivo(true);
        ItemCategory guardada = categoryRepository.save(nueva);
        log.info("Categoría creada con id: {}", guardada.getId());
        return guardada;
    }

    public ItemCategory actualizar(Long id, ItemCategoryDTO dto) {
        log.info("Actualizando categoría con id: {}", id);

        ItemCategory existente = obtenerPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setRequiereDeclaracion(dto.getRequiereDeclaracion());
        existente.setLimiteValorUsd(dto.getLimiteValorUsd());


        ItemCategory actualizada = categoryRepository.save(existente);
        log.info("Categoría {} actualizada correctamente", id);
        return actualizada;
    }

    public void desactivar(Long id) {
        log.info("Desactivando categoría con id: {}", id);
        ItemCategory existente = obtenerPorId(id);
        existente.setActivo(false);
        categoryRepository.save(existente);
        log.info("Categoría {} desactivada correctamente", id);
    }

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

    public List<ItemCategory> obtenerActivas() {
        log.info("Obteniendo categorías activas");
        return categoryRepository.findByActivoTrue();
    }

    public List<ItemCategory> obtenerInactivas() {
        log.info("Obteniendo categorías inactivas");
        return categoryRepository.findByActivoFalse();
    }
    public List<ItemCategory> obtenerRequierenDeclaracion() {
        log.info("Obteniendo categorías que requieren declaración");
        return categoryRepository
                .findByRequiereDeclaracionTrueAndActivoTrue();
    }

    public List<ItemCategory> obtenerSinDeclaracion() {
        log.info("Obteniendo categorías sin declaración especial");
        return categoryRepository
                .findByRequiereDeclaracionFalseAndActivoTrue();
    }

    public List<ItemCategory> buscarPorNombre(String texto) {
        log.info("Buscando categorías con nombre: {}", texto);
        return categoryRepository
                .findByNombreContainingIgnoreCase(texto);
    }

    public List<ItemCategory> obtenerPorLimiteValor(BigDecimal limite) {
        log.info("Obteniendo categorías con límite <= {}", limite);
        return categoryRepository
                .findByLimiteValorUsdLessThanEqual(limite);
    }

    public List<ItemCategory> obtenerActivasOrdenadas() {
        log.info("Obteniendo categorías activas ordenadas por nombre");
        return categoryRepository.findByActivoTrueOrderByNombreAsc();
    }
}