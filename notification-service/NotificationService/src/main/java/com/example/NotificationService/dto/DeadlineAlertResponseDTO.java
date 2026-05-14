// dto/DeadlineAlertResponseDTO.java
package com.example.NotificationService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeadlineAlertResponseDTO {
    private Long id;
    private String mensaje;
    private Integer diasRestantes;
    private String tipoAlerta;
    private Boolean enviada;
    private LocalDateTime createdAt;
}