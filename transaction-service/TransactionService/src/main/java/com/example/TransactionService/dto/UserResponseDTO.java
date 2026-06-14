// Estructura de datos que mapea la respuesta recibida del User Service
package com.example.TransactionService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload que procesa los datos del usuario aduanero importados desde el User Service para validar su solvencia e historial")
public class UserResponseDTO {

    @Schema(
            description = "Identificador único de la cuenta de usuario en el microservicio de origen",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "RUN/RUT de identidad oficial del usuario asociado al trámite",
            example = "12345678-9"
    )
    private String rut;

    @Schema(
            description = "Nombre completo del usuario, viajero o titular de la transacción",
            example = "Benjamin Alexis Riquelme Pozo"
    )
    private String nombre;

    @Schema(
            description = "Dirección de correo electrónico principal del usuario",
            example = "usuario@ejemplo.cl"
    )
    private String email;

    @Schema(
            description = "Nivel de privilegios o rol administrativo/operativo asignado en el sistema",
            example = "VIAJERO"
    )
    private String rol;

    @Schema(
            description = "Indica si la cuenta de usuario está habilitada. Si es 'false', no se pueden emitir transacciones financieras a su nombre.",
            example = "true"
    )
    private Boolean activo;
}