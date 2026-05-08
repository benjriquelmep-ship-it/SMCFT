-- V1__create_entries_table.sql
-- Registra cada ingreso de vehículo al país
-- Un ingreso = un vehículo regresando o entrando por primera vez

CREATE TABLE entries (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Patente del vehículo que ingresa al país
    -- No FK directa — desacoplado de Vehicle Service
    patente          VARCHAR(10)  NOT NULL,

    -- RUT del conductor que realiza el ingreso
    rut_conductor    VARCHAR(12)  NOT NULL,

    -- País de origen del viaje
    pais_origen      VARCHAR(100) NOT NULL,

    -- Paso fronterizo donde se realiza el ingreso
    paso_fronterizo  VARCHAR(100) NOT NULL,

    -- Fecha y hora exacta del ingreso
    fecha_ingreso    TIMESTAMP    NOT NULL,

    -- Tipo de ingreso al país
    -- RETORNO          → vehículo chileno que regresa
    -- ADMISION_TEMPORAL → vehículo extranjero que entra temporalmente
    tipo_ingreso     VARCHAR(30)  NOT NULL,

    -- Estado del ingreso en el proceso de autorización
    -- PENDIENTE  → en proceso de validación
    -- AUTORIZADO → aprobado por el fiscalizador
    -- RECHAZADO  → no autorizado
    estado           VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',

    -- RUT del fiscalizador que procesó el ingreso
    rut_fiscalizador VARCHAR(12)  NULL,

    -- Observaciones del fiscalizador
    observaciones    VARCHAR(500) NULL,

    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);