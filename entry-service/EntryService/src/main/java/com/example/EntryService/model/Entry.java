// model/Entry.java
// Representa cada ingreso de vehículo al país
// Tiene una relación @OneToMany con EntryItem

package com.example.EntryService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// @Entity le dice a JPA que esta clase representa una tabla en MySQL
@Entity
// @Table define el nombre exacto de la tabla
@Table(name = "entries")
// Lombok genera getters, setters, toString, equals y hashCode
@Data
// JPA necesita constructor vacío para crear instancias al leer de la BD
@NoArgsConstructor
// Permite crear objetos con todos los valores en una línea
@AllArgsConstructor
public class Entry {

    // Clave primaria con auto incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patente del vehículo que ingresa
    // No FK directa — Vehicle Service maneja los vehículos
    @Column(nullable = false, length = 10)
    private String patente;

    // RUT del conductor que realiza el ingreso
    @Column(name = "rut_conductor", nullable = false, length = 12)
    private String rutConductor;

    // País de origen del viaje
    @Column(name = "pais_origen", nullable = false, length = 100)
    private String paisOrigen;

    // Paso fronterizo donde ocurre el ingreso
    @Column(name = "paso_fronterizo", nullable = false, length = 100)
    private String pasoFronterizo;

    // Fecha y hora exacta del ingreso
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    // Tipo de ingreso al país
    // "RETORNO" → vehículo chileno que regresa
    // "ADMISION_TEMPORAL" → vehículo extranjero que entra temporalmente
    @Column(name = "tipo_ingreso", nullable = false, length = 30)
    private String tipoIngreso;

    // Estado del ingreso — "PENDIENTE", "AUTORIZADO", "RECHAZADO"
    @Column(nullable = false, length = 30)
    private String estado;

    // RUT del fiscalizador que procesó el ingreso
    @Column(name = "rut_fiscalizador", length = 12)
    private String rutFiscalizador;

    // Observaciones del fiscalizador
    @Column(length = 500)
    private String observaciones;

    // RELACIÓN @OneToMany — un ingreso tiene muchos items declarados
    // mappedBy = "entry" → EntryItem es el dueño de la relación
    // cascade = ALL → guardar/eliminar ingreso afecta sus items
    // fetch = LAZY → los items se cargan solo cuando se necesitan
    @OneToMany(mappedBy = "entry",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    // @JsonManagedReference evita ciclo infinito en serialización JSON
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<EntryItem> items;
}