// dto/VehicleResponseDTO.java
// Representa la respuesta que llega desde Vehicle Service
// cuando Border Crossing verifica el estado del vehículo

package com.example.BorderCrossingService.dto;

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

    // Estado actual del vehículo
    // Debe ser EN_TERRITORIO_NACIONAL para poder registrar una salida
    private String estado;
}