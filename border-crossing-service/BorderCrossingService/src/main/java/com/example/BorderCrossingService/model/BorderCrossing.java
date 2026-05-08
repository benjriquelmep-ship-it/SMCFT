// model/BorderCrossing.java
// Representa cada salida de vehículo del país
// Tiene una relación @OneToMany con BorderCrossingItem

package com.example.BorderCrossingService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// @Entity le dice a JPA que esta clase representa una tabla en MySQL
@Entity
// @Table define el nombre exacto de la tabla
@Table(name = "border_crossings")
// Lombok genera getters, setters, toString, equals y hashCode
@Data
// JPA necesita constructor vacío para crear instancias al leer de la BD
@NoArgsConstructor
// Permite crear objetos con todos los valores en una línea
@AllArgsConstructor
public class BorderCrossing {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patente del vehículo que sale
    // No FK directa — Vehicle Service maneja los vehículos
    @Column(nullable = false, length = 10)
    private String patente;

    // RUT del conductor que realiza el cruce
    @Column(name = "rut_conductor", nullable = false, length = 12)
    private String rutConductor;

    // País de destino del viaje
    @Column(name = "pais_destino", nullable = false, length = 100)
    private String paisDestino;

    // Paso fronterizo donde ocurre el cruce
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    private String pasoFronterizo;

    // Fecha y hora exacta del cruce
    @Column(name = "fecha_cruce", nullable = false)
    private LocalDateTime fechaCruce;

    // Estado del cruce — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    private String estado;

    // RUT del fiscalizador que procesó el cruce
    @Column(name = "rut_fiscalizador", length = 12)
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un cruce tiene muchos items de equipaje
    // mappedBy = "borderCrossing" → BorderCrossingItem es el dueño
    // cascade = ALL → guardar/eliminar cruce afecta sus items
    // fetch = LAZY → los items se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "borderCrossing",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    // @JsonManagedReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<BorderCrossingItem> items;
}