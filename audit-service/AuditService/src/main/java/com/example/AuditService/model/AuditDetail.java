// Representa cada acción registrada en una auditoría
// Tiene una relación @ManyToOne con Audit

package com.example.AuditService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos detalles pertenecen a una auditoría
    @ManyToOne
    @JoinColumn(name = "audit_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Audit audit;

    // Acción auditada
    @Column(nullable = false, length = 100)
    private String accion;

    // Descripción detallada
    @Column(nullable = false, length = 500)
    private String descripcion;

    // RUT del usuario que realizó la acción
    @Column(name = "rut_usuario", nullable = false, length = 12)
    private String rutUsuario;

    // Resultado de la acción
    // "EXITOSO", "FALLIDO", "SOSPECHOSO"
    @Column(nullable = false, length = 30)
    private String resultado;

    // IP desde donde se realizó la acción
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // Fecha y hora de la acción
    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;
}