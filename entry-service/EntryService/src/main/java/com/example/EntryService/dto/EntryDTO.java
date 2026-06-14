// Lo que el cliente manda para registrar un ingreso al país
package com.example.EntryService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para iniciar, procesar y visar el ingreso formal de un vehículo y su conductor al territorio nacional")
public class EntryDTO {

    // Patente del vehículo que ingresa
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(
            description = "Placa patente única identificatoria de la unidad vehicular fiscalizada",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT oficial o pasaporte del conductor de la unidad vehicular",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // País de origen
    @NotBlank(message = "El país de origen es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    @Schema(
            description = "Nación soberana de procedencia desde donde inicia el tránsito hacia la frontera",
            example = "Argentina",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paisOrigen;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100, message = "El paso no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre oficial del complejo aduanero de control perimetral donde se ejecuta el andén de revisión",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora del ingreso
    @NotNull(message = "La fecha de ingreso es obligatoria")
    @Schema(
            description = "Sello de tiempo cronológico (Año-Mes-Día y Hora) de la solicitud formal de acceso",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso
    @NotBlank(message = "El tipo de ingreso es obligatorio")
    @Pattern(
            regexp = "RETORNO|ADMISION_TEMPORAL",
            message = "El tipo debe ser RETORNO o ADMISION_TEMPORAL"
    )
    @Schema(
            description = "Régimen legal aduanero aplicado al vehículo para autorizar su ingreso",
            example = "ADMISION_TEMPORAL",
            allowableValues = {"RETORNO", "ADMISION_TEMPORAL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoIngreso;

    // Estado del ingreso — PENDIENTE, AUTORIZADO o RECHAZADO
    @Schema(
            description = "Situación resolutiva actual del trámite migratorio y aduanero de la orden de ingreso",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "AUTORIZADO", "RECHAZADO"}
    )
    private String estado;
}