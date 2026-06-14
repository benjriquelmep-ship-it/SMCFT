// Representa cada acción registrada en una auditoría
// Tiene una relación @ManyToOne con Audit
package com.example.AuditService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "audit_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa el log pormenorizado de una operación inmutable del andén fronterizo")
public class AuditDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental de la línea de log forense", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "audit_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Instancia de la cabecera de auditoría a la cual queda vinculada este evento", requiredMode = Schema.RequiredMode.REQUIRED)
    private Audit audit;

    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nomenclatura operativa estandarizada que identifica la acción ejecutada por el operador",
            example = "AUTORIZAR_CRUCE",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accion;

    @Column(nullable = false, length = 500)
    @Schema(
            description = "Glosa detallada descriptiva que especifica los cambios de estado o campos inyectados en la transacción",
            example = "El fiscalizador visó la salida del vehículo. Plazo cerrado de forma conforme.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @Column(name = "rut_usuario", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT oficial o pasaporte del usuario operador que gatilló el evento en el andén",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutUsuario;

    @Column(nullable = false, length = 30)
    @Schema(
            description = "Estado o dictamen conclusivo de seguridad asignado a la acción interceptada",
            example = "EXITOSO",
            maxLength = 30,
            allowableValues = {"EXITOSO", "FALLIDO", "SOSPECHOSO"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultado;

    @Column(name = "ip_address", length = 45)
    @Schema(
            description = "Dirección de red IPv4 o IPv6 de la máquina origen que consumió el recurso distribuido",
            example = "192.168.1.50",
            maxLength = 45
    )
    private String ipAddress;

    @Column(name = "fecha_accion", nullable = false)
    @Schema(
            description = "Sello de tiempo exacto (Año-Mes-Día y Hora) en que ocurrió la interacción en los andenes",
            example = "2026-06-12T23:58:34",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaAccion;
}