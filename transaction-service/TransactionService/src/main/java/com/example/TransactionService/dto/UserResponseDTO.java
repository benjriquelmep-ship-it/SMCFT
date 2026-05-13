package com.example.TransactionService.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
}