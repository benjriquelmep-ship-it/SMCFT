// Capa de servicio principal para procesar las reglas de negocio de los reportes
package com.example.ReportService.service;

import com.example.ReportService.dto.BorderCrossingResponseDTO;
import com.example.ReportService.dto.ReportDTO;
import com.example.ReportService.model.Report;
import com.example.ReportService.model.ReportDetail;
import com.example.ReportService.repository.ReportRepository;
import com.example.ReportService.repository.ReportDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportDetailRepository detailRepository;
    private final WebClient webClient;

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    // Obtiene el listado completo de reportes
    public List<Report> obtenerTodos() {
        log.info("Obteniendo todos los reportes");
        return reportRepository.findAll();
    }

    // Busca un reporte por ID o lanza excepción si no existe
    public Report obtenerPorId(Long id) {
        log.info("Buscando reporte con id: {}", id);
        return reportRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reporte con id {} no encontrado", id);
                    return new RuntimeException(
                            "Reporte no encontrado con id: " + id);
                });
    }

    // Crea un reporte en estado GENERANDO, consume WebClient si aplica y lo completa
    public Report generar(ReportDTO dto) {
        log.info("Generando reporte: {}", dto.getTitulo());

        // REGLA DE NEGOCIO: fecha fin posterior a fecha inicio
        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            log.warn("Fecha fin no es posterior a fecha inicio");
            throw new RuntimeException(
                    "La fecha de fin debe ser posterior "
                            + "a la fecha de inicio");
        }

        Report nuevo = new Report();
        nuevo.setTitulo(dto.getTitulo());
        nuevo.setTipoReporte(dto.getTipoReporte());
        nuevo.setFechaInicio(dto.getFechaInicio());
        nuevo.setFechaFin(dto.getFechaFin());
        nuevo.setGeneradoPor(dto.getGeneradoPor());
        nuevo.setEstado("GENERANDO");
        nuevo.setObservaciones(dto.getObservaciones());

        Report guardado = reportRepository.save(nuevo);
        log.info("Reporte creado con id: {}", guardado.getId());

        // Si es reporte de cruces obtener datos de Border Crossing
        if (dto.getTipoReporte().equals("CRUCE_FRONTERIZO")) {
            generarDetallesCruces(guardado);
        }

        // Marcar como completado
        guardado.setEstado("COMPLETADO");
        reportRepository.save(guardado);
        log.info("Reporte {} completado", guardado.getId());
        return guardado;
    }

    // Fuerza la finalización de un reporte que esté en proceso
    public Report completar(Long id) {
        log.info("Completando reporte con id: {}", id);
        Report reporte = obtenerPorId(id);

        // REGLA DE NEGOCIO: solo se completan reportes GENERANDO
        if (!reporte.getEstado().equals("GENERANDO")) {
            log.warn("Reporte {} no está GENERANDO. Estado: {}",
                    id, reporte.getEstado());
            throw new RuntimeException(
                    "Solo se pueden completar reportes en estado GENERANDO. "
                            + "Estado actual: " + reporte.getEstado());
        }

        reporte.setEstado("COMPLETADO");
        Report actualizado = reportRepository.save(reporte);
        log.info("Reporte {} completado correctamente", id);
        return actualizado;
    }

    // Registra una falla en el procesamiento del reporte
    public Report marcarError(Long id, String observaciones) {
        log.info("Marcando reporte {} como error", id);
        Report reporte = obtenerPorId(id);
        reporte.setEstado("ERROR");
        reporte.setObservaciones(observaciones);
        Report actualizado = reportRepository.save(reporte);
        log.info("Reporte {} marcado como error", id);
        return actualizado;
    }

    // Elimina físicamente el reporte de la base de datos
    public void eliminar(Long id) {
        log.info("Eliminando reporte con id: {}", id);
        if (!reportRepository.existsById(id)) {
            log.warn("Reporte con id {} no encontrado", id);
            throw new RuntimeException(
                    "Reporte no encontrado con id: " + id);
        }
        reportRepository.deleteById(id);
        log.info("Reporte {} eliminado correctamente", id);
    }


    // Filtra reportes por su categoría
    public List<Report> obtenerPorTipo(String tipoReporte) {
        log.info("Obteniendo reportes de tipo: {}", tipoReporte);
        return reportRepository.findByTipoReporte(tipoReporte);
    }

    // Busca reportes creados por el RUT de un usuario específico
    public List<Report> obtenerPorGenerador(String generadoPor) {
        log.info("Obteniendo reportes generados por: {}", generadoPor);
        return reportRepository.findByGeneradoPor(generadoPor);
    }

    // Filtra el listado según el estado actual
    public List<Report> obtenerPorEstado(String estado) {
        log.info("Obteniendo reportes con estado: {}", estado);
        return reportRepository.findByEstado(estado);
    }

    // Lista únicamente los reportes finalizados de un tipo específico
    public List<Report> obtenerCompletadosPorTipo(String tipoReporte) {
        log.info("Obteniendo reportes completados de tipo: {}",
                tipoReporte);
        return reportRepository.findByTipoReporteAndEstado(
                tipoReporte, "COMPLETADO");
    }

    // Busca reportes mediante una coincidencia de texto en el título
    public List<Report> buscarPorTitulo(String texto) {
        log.info("Buscando reportes con título: {}", texto);
        return reportRepository
                .findByTituloContainingIgnoreCase(texto);
    }

    // Devuelve los 10 reportes agregados más recientemente
    public List<Report> obtenerUltimosReportes() {
        log.info("Obteniendo los últimos 10 reportes");
        return reportRepository.findTop10ByOrderByIdDesc();
    }

    // Cuenta cuántos reportes tienen un estado específico
    public long contarPorEstado(String estado) {
        log.info("Contando reportes con estado: {}", estado);
        return reportRepository.countByEstado(estado);
    }

    // Cuenta cuántos reportes pertenecen a una categoría
    public long contarPorTipo(String tipoReporte) {
        log.info("Contando reportes de tipo: {}", tipoReporte);
        return reportRepository.countByTipoReporte(tipoReporte);
    }

    // -------------------------------------------------------
    // COMUNICACIÓN CON BORDER CROSSING SERVICE — WebClient
    // -------------------------------------------------------

    // Realiza peticiones HTTP remotas, suma las cantidades y guarda las líneas de detalle
    private void generarDetallesCruces(Report reporte) {
        try {
            log.info("Consultando Border Crossing Service para reporte {}",
                    reporte.getId());

            // Obtener cruces autorizados
            List<BorderCrossingResponseDTO> crucesAutorizados =
                    webClient.get()
                            .uri("/api/v1/border-crossings/estado/AUTORIZADO")
                            .retrieve()
                            .bodyToFlux(BorderCrossingResponseDTO.class)
                            .collectList()
                            .block();

            // Obtener cruces rechazados
            List<BorderCrossingResponseDTO> crucesRechazados =
                    webClient.get()
                            .uri("/api/v1/border-crossings/estado/RECHAZADO")
                            .retrieve()
                            .bodyToFlux(BorderCrossingResponseDTO.class)
                            .collectList()
                            .block();

            // Obtener cruces pendientes
            List<BorderCrossingResponseDTO> crucesPendientes =
                    webClient.get()
                            .uri("/api/v1/border-crossings/estado/PENDIENTE")
                            .retrieve()
                            .bodyToFlux(BorderCrossingResponseDTO.class)
                            .collectList()
                            .block();

            // Crear detalle cruces autorizados
            ReportDetail detAutorizados = new ReportDetail();
            detAutorizados.setReport(reporte);
            detAutorizados.setDescripcion(
                    "Total de cruces autorizados");
            detAutorizados.setValor(new BigDecimal(
                    crucesAutorizados != null
                            ? crucesAutorizados.size() : 0));
            detAutorizados.setUnidad("cruces");
            detAutorizados.setCategoria("AUTORIZADOS");
            detailRepository.save(detAutorizados);

            // Crear detalle cruces rechazados
            ReportDetail detRechazados = new ReportDetail();
            detRechazados.setReport(reporte);
            detRechazados.setDescripcion(
                    "Total de cruces rechazados");
            detRechazados.setValor(new BigDecimal(
                    crucesRechazados != null
                            ? crucesRechazados.size() : 0));
            detRechazados.setUnidad("cruces");
            detRechazados.setCategoria("RECHAZADOS");
            detailRepository.save(detRechazados);

            // Crear detalle cruces pendientes
            ReportDetail detPendientes = new ReportDetail();
            detPendientes.setReport(reporte);
            detPendientes.setDescripcion(
                    "Total de cruces pendientes");
            detPendientes.setValor(new BigDecimal(
                    crucesPendientes != null
                            ? crucesPendientes.size() : 0));
            detPendientes.setUnidad("cruces");
            detPendientes.setCategoria("PENDIENTES");
            detailRepository.save(detPendientes);

            log.info("Detalles de cruces generados para reporte {}",
                    reporte.getId());

        } catch (Exception e) {
            log.error("Error al consultar Border Crossing Service: {}",
                    e.getMessage());
            reporte.setEstado("ERROR");
            reporte.setObservaciones(
                    "Error al obtener datos de Border Crossing Service");
            reportRepository.save(reporte);
        }
    }
}