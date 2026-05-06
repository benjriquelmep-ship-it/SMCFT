package com.example.UserService.model;

// Representa el historial de roles de un usuario
// Tiene una relación @ManyToOne con User
// Muchos roles pertenecen a un usuario
// Esta entidad cumple con IE 2.2.3 — relaciones entre entidades

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN @ManyToOne — muchos roles pertenecen a un usuario
    // @JoinColumn define la columna de clave foránea en la tabla user_roles
    // nullable = false → todo rol debe tener un usuario asociado
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    // @JsonBackReference es el lado inverso de @JsonManagedReference en User
    // Evita el ciclo infinito en la serialización JSON
    @com.fasterxml.jackson.annotation.JsonBackReference
    private User user;

    // Rol asignado en este momento histórico del usuario
    // Permite ver qué roles ha tenido un usuario a lo largo del tiempo
    @Column(nullable = false, length = 50)
    private String rol;

    // Fecha y hora exacta en que se asignó este rol
    @Column(name = "asignado_at")
    private LocalDateTime asignadoAt;

    // Si este rol sigue siendo el activo del usuario
    // activo = false → fue reemplazado por un nuevo rol
    // Solo debe existir un rol con activo = true por usuario a la vez
    @Column(nullable = false)
    private Boolean activo = true;
}
