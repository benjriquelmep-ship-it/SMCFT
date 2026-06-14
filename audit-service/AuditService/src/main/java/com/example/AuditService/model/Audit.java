// Representa cada auditoría del sistema fronterizo
// Tiene una relación @OneToMany con AuditDetail
package com.example.AuditService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad del modelo físico que representa la cabecera e indexación de un proceso de fiscalización forense")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental del proceso de auditoría central", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "rut_auditor", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT legal oficial del fiscalizador u oficial de seguridad encargado de la revisión",
            example = "9876543-2",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutAuditor;

    @Column(name = "tipo_auditoria", nullable = false, length = 50)
    @Schema(
            description = "Ámbito o categoría técnica sobre el cual se desenvuelve la inspección de trazas",
            example = "CRUCE_FRONTERIZO",
            maxLength = 50,
            allowableValues = {"USUARIO", "CRUCE_FRONTERIZO", "TRANSACCION", "SISTEMA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoAuditoria;

    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nombre formal del componente o microservicio distribuido sujeto a evaluación",
            example = "border-crossing-service",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String entidad;

    @Column(name = "entidad_id")
    @Schema(
            description = "Identificador único (ID) de la hilera física de la tabla origen que es objeto del análisis",
            example = "1"
    )
    private Long entidadId;

    @Column(nullable = false, length = 500)
    @Schema(
            description = "Glosa descriptor, alcance u objetivos específicos fijados para la apertura de esta auditoría",
            example = "Auditoría ordinaria sobre validación de aforos físicos de equipaje y tránsitos de salida.",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @Column(nullable = false, length = 30)
    @Schema(
            description = "Situación resolutiva actual en la que se encuentra el expediente de revisión",
            example = "EN_PROCESO",
            maxLength = 30,
            allowableValues = {"EN_PROCESO", "COMPLETADA", "OBSERVACION"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    @Column(name = "fecha_inicio", nullable = false)
    @Schema(
            description = "Sello temporal cronológico exacto que marca el inicio formal de la fiscalización",
            example = "2026-06-12T23:58:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_cierre")
    @Schema(
            description = "Sello temporal cronológico de finalización y cierre del expediente de auditoría (null si sigue abierto)",
            example = "2026-06-13T02:30:00"
    )
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "audit",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Nómina pormenorizada de las líneas de acción y trazas de eventos ligadas a este expediente")
    private List<AuditDetail> detalles;
}