// Entidad JPA que mapea la estructura de la tabla "user_roles" en la base de datos
package com.example.UserService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que registra la vinculación histórica y vigencia de un rol específico asignado a un usuario")
public class UserRole {

    // Identificador único de la asignación del rol histórico
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del registro de rol histórico (Clave primaria autoincremental)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    // RELACIÓN @ManyToOne — muchos roles pertenecen a un usuario
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonBackReference
    @Schema(description = "Datos del usuario asociado a esta asignación de rol")
    private User user;

    // Rol asignado en este momento histórico del usuario
    @Column(nullable = false, length = 50)
    @Schema(
            description = "Perfil de acceso asignado en esta instancia histórica",
            example = "FISCALIZADOR",
            allowableValues = {"ADMINISTRADOR", "FISCALIZADOR", "VIAJERO"},
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String rol;

    // Fecha y hora exacta en que se asignó este rol
    @Column(name = "asignado_at")
    @Schema(
            description = "Fecha y hora exacta en la que se generó la asignación del perfil",
            example = "2026-06-12T22:05:30"
    )
    private LocalDateTime asignadoAt;

    // Si este rol sigue siendo el activo del usuario
    @Column(nullable = false)
    @Schema(
            description = "Indicador de vigencia del rol. 'true' significa que es el rol actual y activo del usuario.",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo = true;
}