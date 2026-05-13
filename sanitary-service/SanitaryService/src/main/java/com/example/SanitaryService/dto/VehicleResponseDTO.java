package com.example.SanitaryService.dto;

import lombok.Data;

@Data
public class VehicleResponseDTO {
    private Long id;
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private String tipoVehiculo;
    private String rutPropietario;
    private String estado;
}