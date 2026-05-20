// Entidad JPA que mapea la estructura de la tabla "vehicles" en la base de datos
package com.example.VehicleService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// @Entity le dice a JPA que esta clase representa una tabla en MySQL
@Entity
// @Table define el nombre exacto de la tabla
@Table(name = "vehicles")
// Lombok genera getters, setters, toString, equals y hashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    // Identificador único autoincremental del parque automotor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patente única del vehículo
    // unique = true → no pueden existir dos vehículos con la misma patente
    @Column(nullable = false, unique = true, length = 10)
    private String patente;

    // Fabricante o marca de la unidad automotriz
    @Column(nullable = false, length = 50)
    private String marca;

    // Línea o modelo específico del vehículo
    @Column(nullable = false, length = 50)
    private String modelo;

    // Año de fabricación de la estructura vehicular
    @Column(nullable = false)
    private Integer anio;

    // Tipo del vehículo según clasificación aduanera
    // "PARTICULAR", "DIPLOMATICO", "COMERCIAL"
    @Column(name = "tipo_vehiculo", nullable = false, length = 30)
    private String tipoVehiculo;

    // Solo guardamos el RUT del propietario, no el objeto User completo
    // Esto mantiene los microservicios desacoplados entre sí
    // Si necesitamos más datos del propietario consultamos a User Service
    @Column(name = "rut_propietario", nullable = false, length = 12)
    private String rutPropietario;

    // Estado actual del vehículo en el sistema fronterizo
    // "EN_TERRITORIO_NACIONAL", "FUERA_DEL_PAIS", "ADMISION_TEMPORAL"
    @Column(nullable = false, length = 30)
    private String estado;

    // RELACIÓN @OneToMany — un vehículo tiene muchos documentos
    // mappedBy = "vehicle" → VehicleDocument es el dueño de la relación
    // cascade = ALL → guardar/eliminar Vehicle afecta sus documentos
    // fetch = LAZY → los documentos se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    // @JsonManagedReference evita ciclo infinito en la serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<VehicleDocument> documentos;
}