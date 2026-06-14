// Representa la tabla deadline_alerts en la base de datos
// Una alerta = aviso que se genera cuando un deadline está próximo a vencer
// Tiene una relación @ManyToOne con Deadline : Muchas alertas pertenecen a un solo deadline
package com.example.DeadlineService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "deadline_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa una instancia de alerta gatillada por proximidad de vencimiento temporal")
public class DeadlineAlert {

    // Id de la alerta — se genera automáticamente en la BD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental de la alerta de plazo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // RELACIÓN @ManyToOne — muchas alertas pertenecen a un deadline
    @ManyToOne
    @JoinColumn(name = "deadline_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia del plazo de control cronológico de cabecera asociada a esta alerta", requiredMode = Schema.RequiredMode.REQUIRED)
    private Deadline deadline;

    // Mensaje descriptivo de la alerta
    @Column(nullable = false, length = 500)
    @Schema(
            description = "Mensaje o glosa explícita que describe la proximidad de la infracción",
            example = "Alerta de control: Quedan menos de 72 horas para la expiración del plazo máximo.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;

    // Cuántos días le quedaban al deadline cuando se generó la alerta
    @Column(name = "dias_restantes", nullable = false)
    @Schema(
            description = "Días calendario faltantes calculados al momento exacto de emitirse el evento",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer diasRestantes;

    // Tipo de alerta según la urgencia
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    @Schema(
            description = "Nivel de criticidad u oportunidad temporal asignado a la alerta",
            example = "URGENTE",
            maxLength = 30,
            allowableValues = {"AVISO", "URGENTE", "VENCIDO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoAlerta;

    // Si la alerta ya fue enviada al Notification Service
    @Column(nullable = false)
    @Schema(
            description = "Bandera de sincronización lógica que indica si el aviso ya fue despachado de forma exitosa al Notification Service",
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean enviada = false;

    // Fecha y hora en que se creó la alerta
    @Column(name = "created_at")
    @Schema(
            description = "Sello temporal (Año-Mes-Día y Hora) del cálculo automático y disparo de la alerta",
            example = "2026-06-12T23:31:50"
    )
    private LocalDateTime createdAt;
}