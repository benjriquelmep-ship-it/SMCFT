package com.example.AuditService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j // Agrega esto para habilitar el objeto 'log'
public class TuServicio {
    public void procesar() {
        log.info("Iniciando proceso de auditoría"); // Registro informativo
        try {
            log.debug("Buscando datos en la base de datos de XAMPP");
        } catch (Exception e) {
            log.error("Error crítico en el servicio", e); // Registro de error
        }
    }
}