// dto/VehicleResponseDTO.java
// Representa la respuesta que llega desde Vehicle Service
// cuando Entry Service verifica el estado del vehículo

package com.example.EntryService.dto;

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
    // Para RETORNO debe ser FUERA_DEL_PAIS
    // Para ADMISION_TEMPORAL puede ser cualquier estado
    private String estado;
}