// model/Audit.java
// Representa cada auditoría del sistema fronterizo
// Tiene una relación @OneToMany con AuditDetail

package com.example.AuditService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RUT del auditor que realiza la revisión
    @Column(name = "rut_auditor", nullable = false, length = 12)
    private String rutAuditor;

    // Tipo de auditoría
    // "USUARIO", "CRUCE_FRONTERIZO", "TRANSACCION", "SISTEMA"
    @Column(name = "tipo_auditoria", nullable = false, length = 50)
    private String tipoAuditoria;

    // Entidad auditada
    @Column(nullable = false, length = 100)
    private String entidad;

    // ID del registro auditado
    @Column(name = "entidad_id")
    private Long entidadId;

    // Descripción de la auditoría
    @Column(nullable = false, length = 500)
    private String descripcion;

    // Estado de la auditoría
    // "EN_PROCESO", "COMPLETADA", "OBSERVACION"
    @Column(nullable = false, length = 30)
    private String estado;

    // Fecha de inicio
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    // Fecha de cierre
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    // RELACIÓN @OneToMany — una auditoría tiene muchos detalles
    @OneToMany(mappedBy = "audit",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<AuditDetail> detalles;
}