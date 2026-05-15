// dto/BorderCrossingResponseDTO.java
package com.example.ReportService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorderCrossingResponseDTO {
    private Long id;
    private String patente;
    private String rutConductor;
    private String paisDestino;
    private String pasoFronterizo;
    private LocalDateTime fechaCruce;
    private String estado;
    private String rutFiscalizador;
}