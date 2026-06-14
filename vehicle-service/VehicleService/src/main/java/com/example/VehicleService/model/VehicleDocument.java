// Entidad JPA que mapea la estructura de la tabla "vehicle_documents" en la base de datos
package com.example.VehicleService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "vehicle_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa las acreditaciones legales (permisos, seguros, revisiones) exigidas para el tránsito de un vehículo")
public class VehicleDocument {

    // Identificador único autoincremental del documento vehicular
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único de la pieza documental (Clave primaria autoincremental)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // RELACIÓN @ManyToOne — muchos documentos pertenecen a un vehículo
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Ficha técnica del vehículo al cual se encuentra indexado este documento")
    private Vehicle vehicle;

    // Tipo de documento aduanero
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Naturaleza o tipo de la acreditación legal presentada",
            example = "REVISION_TECNICA",
            allowableValues = {"PERMISO_CIRCULACION", "SEGURO_OBLIGATORIO", "REVISION_TECNICA"},
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    // Número único del documento — no pueden existir dos con el mismo número
    @Column(nullable = false, unique = true, length = 50)
    @Schema(
            description = "Número de folio o serie único impreso en el documento legal",
            example = "FOLIO-987654",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String numero;

    // Fecha de vencimiento del documento
    @Column(name = "fecha_vencimiento", nullable = false)
    @Schema(
            description = "Fecha límite calendario que dictamina la caducidad y vigencia de la credencial",
            example = "2027-04-30",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate fechaVencimiento;

    // Si el documento está vigente o fue invalidado
    @Column(nullable = false)
    @Schema(
            description = "Estado lógico de validez de la pieza. 'true' indica documento legalmente operativo, 'false' indica anulado o expirado.",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean vigente = true;
}