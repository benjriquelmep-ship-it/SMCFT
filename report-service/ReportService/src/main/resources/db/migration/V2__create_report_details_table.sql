-- V2__create_report_details_table.sql
-- Detalles de cada reporte generado
-- Relación: muchos detalles pertenecen a un reporte
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE report_details (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia reports
    report_id   BIGINT        NOT NULL,

    -- Descripción del dato reportado
    descripcion VARCHAR(300)  NOT NULL,

    -- Valor numérico del dato
    valor       DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- Unidad del valor
    unidad      VARCHAR(50)   NOT NULL DEFAULT 'unidad',

    -- Categoría del dato dentro del reporte
    categoria   VARCHAR(100)  NOT NULL,

    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_report_details
        FOREIGN KEY (report_id)
        REFERENCES reports(id)
        ON DELETE CASCADE
);