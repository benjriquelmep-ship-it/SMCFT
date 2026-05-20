// Este archivo es el DTO de respuesta del Vehicle Service
// Representa los datos que llegan cuando Sanitary Service
// consulta un vehículo por su patente

package com.example.SanitaryService.dto;

import lombok.Data;

@Data
public class VehicleResponseDTO {

    // Id del vehículo en la BD de Vehicle Service
    // Sanitary Service no lo usa directamente
    // pero llega en la respuesta
    private Long id;

    // Patente del vehículo
    // Sanitary Service la usó para consultar este vehículo
    private String patente;

    // Marca del vehículo
    private String marca;

    // Modelo del vehículo
    private String modelo;

    // Año de fabricación del vehículo
    private Integer anio;

    // Tipo de vehículo según su clasificación
    private String tipoVehiculo;

    // RUT del propietario del vehículo
    private String rutPropietario;

    // Estado actual del vehículo en el sistema fronterizo
    // Sanitary Service lo lee para verificar si el vehículo
    // puede ser inspeccionado
    private String estado;
}