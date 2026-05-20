// Entidad JPA que mapea la estructura de la tabla "vehicle_documents" en la base de datos
package com.example.VehicleService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDocument {

    // Identificador único autoincremental del documento vehicular
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos documentos pertenecen a un vehículo
    // @JoinColumn define la columna de clave foránea en vehicle_documents
    // nullable = false → todo documento debe tener un vehículo asociado
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    // @JsonBackReference evita el ciclo infinito en la serialización JSON
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Vehicle vehicle;

    // Tipo de documento aduanero
    // "PERMISO_CIRCULACION", "SEGURO_OBLIGATORIO", "REVISION_TECNICA"
    @Column(nullable = false, length = 50)
    private String tipo;

    // Número único del documento — no pueden existir dos con el mismo número
    @Column(nullable = false, unique = true, length = 50)
    private String numero;

    // Fecha de vencimiento del documento
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // Si el documento está vigente o fue invalidado
    // vigente = false → documento vencido o anulado
    @Column(nullable = false)
    private Boolean vigente = true;
}