// Objeto de transferencia de datos que procesa la respuesta entrante desde User Service
package com.example.VehicleService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload que procesa la información de identidad del propietario retornada por el User Service para validaciones cruzadas")
public class UserResponseDTO {

    @Schema(
            description = "Identificador único de la cuenta de usuario en el sistema de origen",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "RUN/RUT de identidad chileno del titular verificado",
            example = "12345678-9"
    )
    private String rut;

    @Schema(
            description = "Nombre completo o razón social del propietario",
            example = "Benjamin Alexis Riquelme Pozo"
    )
    private String nombre;

    @Schema(
            description = "Dirección de correo electrónico institucional o personal del dueño",
            example = "usuario@ejemplo.cl"
    )
    private String email;

    @Schema(
            description = "Perfil de acceso o rol principal asignado en la plataforma",
            example = "VIAJERO"
    )
    private String rol;

    @Schema(
            description = "Estado de vigencia del usuario. Si es 'false', la cuenta no está autorizada para matricular vehículos.",
            example = "true"
    )
    private Boolean activo;
}