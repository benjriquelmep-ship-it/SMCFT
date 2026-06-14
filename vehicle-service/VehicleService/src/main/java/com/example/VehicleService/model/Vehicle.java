// Entidad JPA que mapea la estructura de la tabla "vehicles" en la base de datos
package com.example.VehicleService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una unidad automotriz registrada y fiscalizada dentro del sistema de control fronterizo")
public class Vehicle {

    // Identificador único autoincremental del parque automotor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del vehículo (Clave primaria autoincremental)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // Patente única del vehículo
    @Column(nullable = false, unique = true, length = 10)
    @Schema(
            description = "Placa patente única de identificación del vehículo (ej. formato chileno)",
            example = "ABCD-12",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String patente;

    // Fabricante o marca de la unidad automotriz
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Fabricante o marca de la unidad vehicular",
            example = "Suzuki",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String marca;

    // Línea o modelo específico del vehículo
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Modelo específico o línea comercial del vehículo",
            example = "Grand Vitara",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String modelo;

    // Año de fabricación de la estructura vehicular
    @Column(nullable = false)
    @Schema(
            description = "Año calendario de fabricación de la unidad",
            example = "2024",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer anio;

    // Tipo del vehículo según clasificación aduanera
    @Column(name = "tipo_vehiculo", nullable = false, length = 30)
    @Schema(
            description = "Categoría de uso y clasificación aduanera asignada",
            example = "PARTICULAR",
            allowableValues = {"PARTICULAR", "DIPLOMATICO", "COMERCIAL"},
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoVehiculo;

    // Solo guardamos el RUT del propietario, no el objeto User completo
    @Column(name = "rut_propietario", nullable = false, length = 12)
    @Schema(
            description = "RUN/RUT del propietario legal registrado (permite el desacoplamiento con User Service)",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rutPropietario;

    // Estado actual del vehículo en el sistema fronterizo
    @Column(nullable = false, length = 30)
    @Schema(
            description = "Situación aduanera y ubicación de tránsito actual del móvil",
            example = "EN_TERRITORIO_NACIONAL",
            allowableValues = {"EN_TERRITORIO_NACIONAL", "FUERA_DEL_PAIS", "ADMISION_TEMPORAL"},
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String estado;

    // RELACIÓN @OneToMany — un vehículo tiene muchos documentos
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección de credenciales y acreditaciones documentales adjuntas a la unidad")
    private List<VehicleDocument> documentos;
}