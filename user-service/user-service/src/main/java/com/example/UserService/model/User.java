// Entidad JPA que mapea la estructura de la tabla "users" en la base de datos
package com.example.UserService.model;

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
@Schema(description = "Entidad que representa a un usuario (ciudadano o funcionario) registrado en el sistema de control fronterizo SMCFT")
public class User {

    // Clave primaria autoincremental de la tabla de usuarios
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del usuario (Clave primaria autoincremental)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // RUN único del usuario (largo máximo 12 caracteres)
    @Column(nullable = false, unique = true, length = 12)
    @Schema(
            description = "RUN/RUT de identidad del usuario en formato chileno con guión y dígito verificador",
            example = "12345678-9",
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rut;

    // Nombre completo o razón social registrada en el sistema
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nombre completo o razón social del usuario registrado",
            example = "Benjamin Alexis Riquelme Pozo",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    // Dirección de correo electrónico única para el inicio de sesión
    @Column(nullable = false, unique = true, length = 150)
    @Schema(
            description = "Dirección de correo electrónico institucional o personal única",
            example = "usuario@ejemplo.cl",
            maxLength = 150,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    // Hash de la contraseña secreta encriptada
    @Column(nullable = false)
    @Schema(
            description = "Hash secreto de la contraseña encriptada del usuario",
            example = "$2a$10$X5vK...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    // Perfil de accesos principal asignado actualmente
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Perfil de acceso principal asignado y activo en el sistema",
            example = "VIAJERO",
            allowableValues = {"ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"},
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rol; // "ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"

    // Indicador de vigencia lógica de la cuenta de usuario (soft delete)
    @Column(nullable = false)
    @Schema(
            description = "Estado de vigencia lógica de la cuenta. 'true' indica cuenta habilitada, 'false' indica baja (Soft Delete).",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo = true;

    // RELACIÓN @OneToMany — un usuario tiene muchos roles históricos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Schema(description = "Colección histórica de todos los perfiles que han sido asignados al usuario")
    private List<UserRole> roles;
}