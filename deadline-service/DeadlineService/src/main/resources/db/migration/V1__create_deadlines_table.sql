-- V1__create_deadlines_table.sql
-- Usa DATETIME en vez de TIMESTAMP para evitar conflictos con MySQL

CREATE TABLE deadlines (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- Patente del vehículo al que aplica el deadline
    patente          VARCHAR(10)  NOT NULL,

    -- RUT del conductor responsable
    rut_conductor    VARCHAR(12)  NOT NULL,

    -- ID del ingreso en Entry Service que originó este deadline
    entry_id         BIGINT       NOT NULL,

    -- Fecha de ingreso al país — DATETIME para evitar error MySQL
    fecha_ingreso    DATETIME     NOT NULL,

    -- Fecha límite de permanencia — DATETIME para evitar error MySQL
    fecha_limite     DATETIME     NOT NULL,

    -- Tipo de deadline
    -- ADMISION_TEMPORAL   → vehículo extranjero con plazo
    -- RETORNO_OBLIGATORIO → vehículo que debe regresar
    tipo             VARCHAR(50)  NOT NULL,

    -- Estado del deadline
    -- ACTIVO, VENCIDO, CERRADO
    estado           VARCHAR(30)  NOT NULL DEFAULT 'ACTIVO',

    -- Observaciones adicionales
    observaciones    VARCHAR(500) NULL,

    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP
);