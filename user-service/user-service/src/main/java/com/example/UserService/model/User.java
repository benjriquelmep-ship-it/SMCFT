// Entidad JPA que mapea la estructura de la tabla "users" en la base de datos
package com.example.UserService.model;

// Esta clase representa la tabla "users" en la base de datos


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor

@Schema(description = "Entidad que representa user")
public class User {

    // Clave primaria autoincremental de la tabla de usuarios
    @Id                                                    // Esta es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Auto incremento en MySQL
    @Schema(description = "Id", example = "1")
    private Long id;

    // RUN único del usuario (largo máximo 12 caracteres)
    @Column(nullable = false, unique = true, length = 12)
    @Schema(description = "Rut", example = "12345678-9", maxLength = 12)
    private String rut;

    // Nombre completo o razón social registrada en el sistema
    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre", example = "Juan Pérez", maxLength = 100)
    private String nombre;

    // Dirección de correo electrónico única para el inicio de sesión
    @Column(nullable = false, unique = true, length = 150)
    @Schema(description = "Email", example = "usuario@ejemplo.cl", maxLength = 150)
    private String email;

    // Hash de la contraseña secreta encriptada
    @Column(nullable = false)
    @Schema(description = "Password", example = "********")
    private String password;

    // Perfil de accesos principal asignado actualmente
    @Column(nullable = false, length = 50)
    @Schema(description = "Rol", example = "VIAJERO", maxLength = 50)
    private String rol; // "ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"

    // Indicador de vigencia lógica de la cuenta de usuario (soft delete)
    @Column(nullable = false)
    @Schema(description = "Activo", example = "true")
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