// Estructura de datos que mapea la respuesta recibida del Border Crossing Service
package com.example.ReportService.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico que procesa el registro histórico de un cruce vehicular importado desde el Border Crossing Service")
public class BorderCrossingResponseDTO {

    // Identificador único del cruce en el microservicio origen
    @Schema(
            description = "Identificador único del registro de cruce en el sistema origen (Clave primaria)",
            example = "1"
    )
    private Long id;

    // Placa patente del vehículo consultado
    @Schema(
            description = "Placa patente única de identificación del vehículo que efectuó el paso",
            example = "ABCD-12"
    )
    private String patente;

    // RUN o pasaporte del conductor que realiza el paso
    @Schema(
            description = "RUN/RUT o pasaporte del conductor que guiaba la unidad",
            example = "12345678-9"
    )
    private String rutConductor;

    // Nación de destino del viaje
    @Schema(
            description = "País soberano de destino hacia donde se dirige el flujo vehicular",
            example = "Argentina"
    )
    private String paisDestino;

    // Nombre del complejo o paso aduanero
    @Schema(
            description = "Nombre oficial del complejo fronterizo donde se registró el movimiento",
            example = "Los Libertadores"
    )
    private String pasoFronterizo;

    // Timestamp exacto del registro
    @Schema(
            description = "Sello de tiempo cronológico exacto en que se autorizó o denegó el paso",
            example = "2026-06-12T23:15:00"
    )
    private LocalDateTime fechaCruce;

    // Condición actual del trámite (PENDIENTE, AUTORIZADO, RECHAZADO)
    @Schema(
            description = "Dictamen o situación resolutiva final del flujo migratorio/aduanero",
            example = "AUTORIZADO",
            allowableValues = {"PENDIENTE", "AUTORIZADO", "RECHAZADO"}
    )
    private String estado;

    // Identificador del funcionario aduanero a cargo
    @Schema(
            description = "RUN/RUT del fiscalizador aduanero que procesó el trámite en caseta",
            example = "9876543-2"
    )
    private String rutFiscalizador;
}