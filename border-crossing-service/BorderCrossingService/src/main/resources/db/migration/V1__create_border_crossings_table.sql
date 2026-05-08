-- V1__create_border_crossings_table.sql
-- Registra cada salida de vehículo del país
-- Un cruce = un vehículo saliendo en una fecha determinada

CREATE TABLE border_crossings (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Patente del vehículo que sale del país
    -- No FK directa — desacoplado de Vehicle Service
    patente          VARCHAR(10)  NOT NULL,

    -- RUT del conductor que realiza el cruce
    rut_conductor    VARCHAR(12)  NOT NULL,

    -- País de destino del viaje
    pais_destino     VARCHAR(100) NOT NULL,

    -- Paso fronterizo donde se realiza el cruce
    -- Ej: Los Libertadores, Chacalluta, Cardenal Samoré
    paso_fronterizo  VARCHAR(100) NOT NULL,

    -- Fecha y hora exacta del cruce
    fecha_cruce      TIMESTAMP    NOT NULL,

    -- Estado del cruce en el proceso de autorización
    -- PENDIENTE  → en proceso de validación por el fiscalizador
    -- AUTORIZADO → aprobado por el fiscalizador
    -- RECHAZADO  → no autorizado por el fiscalizador
    estado           VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',

    -- RUT del fiscalizador que procesó el cruce
    -- null si aún está PENDIENTE
    rut_fiscalizador VARCHAR(12)  NULL,

    -- Observaciones del fiscalizador al revisar el cruce
    observaciones    VARCHAR(500) NULL,

    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);