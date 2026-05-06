package com.example.UserService.model;

// Esta clase representa la tabla "users" en la base de datos


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity                    // Le dice a JPA que esta clase es una tabla
@Table(name = "users")     // Nombre exacto de la tabla en MySQL
@Data                      // Lombok genera getters, setters, toString automáticamente
@NoArgsConstructor         // Lombok genera constructor vacío
@AllArgsConstructor        // Lombok genera constructor con todos los campos

public class User {

    @Id                                                    // Esta es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Auto incremento en MySQL
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String rol; // "ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"

    @Column(nullable = false)
    private Boolean activo = true;

    // RELACIÓN @OneToMany — un usuario tiene muchos roles históricos
    // mappedBy = "user" → UserRole es el dueño de la relación (tiene la FK)
    // cascade = ALL → guardar/eliminar User afecta sus UserRole
    // fetch = LAZY → los roles se cargan solo cuando se necesitan (eficiencia)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    // @JsonManagedReference evita el ciclo infinito JSON:
    // User → roles → UserRole → user → User → ... (ciclo infinito)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<UserRole> roles;

}
