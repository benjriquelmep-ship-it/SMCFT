-- V1__create_reports_table.sql
-- Registra los reportes generados en el sistema fronterizo

CREATE TABLE reports (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- Título descriptivo del reporte
    titulo       VARCHAR(200) NOT NULL,

    -- Tipo de reporte
    -- CRUCE_FRONTERIZO, ADMISION_TEMPORAL, VEHICULOS, USUARIOS
    tipo_reporte VARCHAR(50)  NOT NULL,

    -- Período del reporte
    fecha_inicio DATETIME     NOT NULL,
    fecha_fin    DATETIME     NOT NULL,

    -- RUT del administrador que generó el reporte
    generado_por VARCHAR(12)  NOT NULL,

    -- Estado del reporte
    -- GENERANDO, COMPLETADO, ERROR
    estado       VARCHAR(30)  NOT NULL DEFAULT 'GENERANDO',

    -- Observaciones adicionales
    observaciones VARCHAR(500) NULL,

    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP
);