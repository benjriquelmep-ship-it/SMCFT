// Lo que el cliente manda para registrar un nuevo cruce fronterizo
package com.example.BorderCrossingService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Estructura de datos requerida para iniciar, procesar y autorizar una solicitud de cruce o salida vehicular del país")
public class BorderCrossingDTO {

    // Patente del vehículo que sale del país
    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede tener más de 10 caracteres")
    @Schema(
            description = "Placa patente única identificatoria de la unidad vehicular sujeta a control de salida",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // RUT del conductor
    @NotBlank(message = "El RUT del conductor es obligatorio")
    @Size(max = 12, message = "El RUT no puede tener más de 12 caracteres")
    @Schema(
            description = "RUN/RUT oficial o documento de pasaporte del conductor de la unidad vehicular",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutConductor;

    // País de destino
    @NotBlank(message = "El país de destino es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    @Schema(
            description = "Nación soberana de destino final hacia donde se dirige el tránsito perimetral",
            example = "Argentina",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paisDestino;

    // Paso fronterizo
    @NotBlank(message = "El paso fronterizo es obligatorio")
    @Size(max = 100, message = "El paso no puede tener más de 100 caracteres")
    @Schema(
            description = "Nombre oficial del complejo aduanero fronterizo donde se ejecuta físicamente el andén de salida",
            example = "Los Libertadores",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pasoFronterizo;

    // Fecha y hora del cruce
    @NotNull(message = "La fecha del cruce es obligatoria")
    @Schema(
            description = "Sello temporal cronológico (Año-Mes-Día y Hora) del momento de control del cruce",
            example = "2026-06-12T23:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaCruce;
}