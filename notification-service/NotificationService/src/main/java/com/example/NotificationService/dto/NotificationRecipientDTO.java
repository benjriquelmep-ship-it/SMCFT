// dto/NotificationRecipientDTO.java
package com.example.NotificationService.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotificationRecipientDTO {

    @NotNull(message = "El ID de la notificación es obligatorio")
    private Long notificationId;

    @NotBlank(message = "El RUT del destinatario es obligatorio")
    @Size(max = 12,
            message = "El RUT no puede tener más de 12 caracteres")
    private String rutDestinatario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene formato válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100,
            message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
}