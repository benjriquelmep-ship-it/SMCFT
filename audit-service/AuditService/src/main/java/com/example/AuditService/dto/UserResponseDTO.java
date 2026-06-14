package com.example.AuditService.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Payload analítico de integración que expone el estado y metadatos del operador importados desde el User Service")
public class UserResponseDTO {

    @Schema(description = "Identificador único (ID) del usuario fiscalizador en origen", example = "1")
    private Long id;

    @Schema(description = "RUN/RUT legal identificatorio del operador aduanero", example = "12345678-9")
    private String rut;

    @Schema(description = "Nombre y apellidos completos del funcionario registrado", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Correo electrónico institucional de control que actúa como identificador", example = "fiscalizador@aduana.cl")
    private String email;

    @Schema(description = "Rol operativo que ejerce el funcionario dentro del andén de control", example = "FISCALIZADOR")
    private String rol;

    @Schema(description = "Flag de vigencia administrativa; si es falso, el operador está desautorizado para emitir o registrar trazas", example = "true")
    private Boolean activo;
}