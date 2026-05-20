// Capa de servicio que procesa las reglas de negocio de las líneas de detalle
package com.example.ReportService.service;

import com.example.ReportService.dto.ReportDetailDTO;
import com.example.ReportService.model.Report;
import com.example.ReportService.model.ReportDetail;
import com.example.ReportService.repository.ReportDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportDetailService {

    private final ReportDetailRepository detailRepository;
    private final ReportService reportService;


    // Devuelve todos los registros guardados en la tabla
    public List<ReportDetail> obtenerTodos() {
        log.info("Obteniendo todos los detalles");
        return detailRepository.findAll();
    }

    // Busca un detalle o levanta excepción si no existe
    public ReportDetail obtenerPorId(Long id) {
        log.info("Buscando detalle con id: {}", id);
        return detailRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Detalle con id {} no encontrado", id);
                    return new RuntimeException(
                            "Detalle no encontrado con id: " + id);
                });
    }

    // Valida y asocia un nuevo detalle al reporte padre
    public ReportDetail agregar(ReportDetailDTO dto) {
        log.info("Agregando detalle al reporte: {}", dto.getReportId());

        // REGLA DE NEGOCIO 1: el reporte debe existir
        Report reporte = reportService.obtenerPorId(dto.getReportId());

        // REGLA DE NEGOCIO 2: no se agregan detalles a reportes ERROR
        if (reporte.getEstado().equals("ERROR")) {
            log.warn("No se pueden agregar detalles a reporte ERROR");
            throw new RuntimeException(
                    "No se pueden agregar detalles "
                            + "a reportes en estado ERROR");
        }

        ReportDetail detalle = new ReportDetail();
        detalle.setReport(reporte);
        detalle.setDescripcion(dto.getDescripcion());
        detalle.setValor(dto.getValor());
        detalle.setUnidad(dto.getUnidad());
        detalle.setCategoria(dto.getCategoria());

        ReportDetail guardado = detailRepository.save(detalle);
        log.info("Detalle agregado con id: {}", guardado.getId());
        return guardado;
    }

    // Modifica los campos de un detalle existente
    public ReportDetail actualizar(Long id, ReportDetailDTO dto) {
        log.info("Actualizando detalle con id: {}", id);
        ReportDetail existente = obtenerPorId(id);

        existente.setDescripcion(dto.getDescripcion());
        existente.setValor(dto.getValor());
        existente.setUnidad(dto.getUnidad());
        existente.setCategoria(dto.getCategoria());

        ReportDetail actualizado = detailRepository.save(existente);
        log.info("Detalle {} actualizado correctamente", id);
        return actualizado;
    }

    // Borra físicamente un detalle si el ID es válido
    public void eliminar(Long id) {
        log.info("Eliminando detalle con id: {}", id);
        if (!detailRepository.existsById(id)) {
            log.warn("Detalle con id {} no encontrado", id);
            throw new RuntimeException(
                    "Detalle no encontrado con id: " + id);
        }
        detailRepository.deleteById(id);
        log.info("Detalle {} eliminado correctamente", id);
    }


    // Busca los detalles pertenecientes a un mismo reporte
    public List<ReportDetail> obtenerPorReporte(Long reportId) {
        log.info("Obteniendo detalles del reporte: {}", reportId);
        return detailRepository.findByReportId(reportId);
    }

    // Agrupa detalles según su etiqueta o categoría
    public List<ReportDetail> obtenerPorCategoria(String categoria) {
        log.info("Obteniendo detalles de categoría: {}", categoria);
        return detailRepository.findByCategoria(categoria);
    }

    // Filtra detalles cruzando el ID del reporte y la categoría
    public List<ReportDetail> obtenerPorReporteYCategoria(
            Long reportId, String categoria) {
        log.info("Obteniendo detalles de reporte {} categoría {}",
                reportId, categoria);
        return detailRepository.findByReportIdAndCategoria(
                reportId, categoria);
    }

    // Busca coincidencias de texto parcial ignorando mayúsculas
    public List<ReportDetail> buscarPorDescripcion(String descripcion) {
        log.info("Buscando detalles con descripción: {}", descripcion);
        return detailRepository
                .findByDescripcionContainingIgnoreCase(descripcion);
    }

    // Devuelve las líneas del reporte ordenadas por valor descendente
    public List<ReportDetail> obtenerPorReporteOrdenados(Long reportId) {
        log.info("Obteniendo detalles del reporte {} ordenados",
                reportId);
        return detailRepository
                .findByReportIdOrderByValorDesc(reportId);
    }

    // Obtiene una lista con las últimas 10 inserciones
    public List<ReportDetail> obtenerUltimosDetalles() {
        log.info("Obteniendo los últimos 10 detalles");
        return detailRepository.findTop10ByOrderByIdDesc();
    }
}