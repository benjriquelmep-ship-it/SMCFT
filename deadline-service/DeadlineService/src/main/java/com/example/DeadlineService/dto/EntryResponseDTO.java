package com.example.DeadlineService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EntryResponseDTO {
    private Long id;
    private String patente;
    private String rutConductor;
    private String paisOrigen;
    private String pasoFronterizo;
    private LocalDateTime fechaIngreso;
    private String tipoIngreso;
    private String estado;
}