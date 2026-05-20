// Estructura de datos que mapea la respuesta recibida del Border Crossing Service
package com.example.ReportService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorderCrossingResponseDTO {
    // Identificador único del cruce en el microservicio origen
    private Long id;

    // Placa patente del vehículo consultado
    private String patente;

    // RUN o pasaporte del conductor que realiza el paso
    private String rutConductor;

    // Nación de destino del viaje
    private String paisDestino;

    // Nombre del complejo o paso aduanero
    private String pasoFronterizo;

    // Timestamp exacto del registro
    private LocalDateTime fechaCruce;

    // Condición actual del trámite (PENDIENTE, AUTORIZADO, RECHAZADO)
    private String estado;

    // Identificador del funcionario aduanero a cargo
    private String rutFiscalizador;
}