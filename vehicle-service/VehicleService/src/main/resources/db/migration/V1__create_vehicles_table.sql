-- Flyway ejecuta este script automáticamente al iniciar el Vehicle Service
-- Esta tabla guarda los vehículos que cruzan la frontera

CREATE TABLE vehicles (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Patente del vehículo — identificador único
    -- No pueden existir dos vehículos con la misma patente
    patente          VARCHAR(10)  NOT NULL UNIQUE,

    -- Datos del vehículo
    marca            VARCHAR(50)  NOT NULL,
    modelo           VARCHAR(50)  NOT NULL,
    anio             INT          NOT NULL,

    -- Tipo según dominio aduanero del sistema fronterizo
    -- PARTICULAR  → ciudadano común
    -- DIPLOMATICO → personal diplomático con título de salida temporal
    -- COMERCIAL   → vehículo de carga o transporte comercial
    tipo_vehiculo    VARCHAR(30)  NOT NULL,

    -- RUT del propietario — no guardamos el objeto User completo
    -- Solo el RUT para mantener los microservicios desacoplados
    -- Vehicle Service consulta a User Service cuando necesita más datos del propietario
    rut_propietario  VARCHAR(12)  NOT NULL,

    -- Estado actual del vehículo en el sistema fronterizo
    -- EN_TERRITORIO_NACIONAL → está dentro del país
    -- FUERA_DEL_PAIS         → salió temporalmente
    -- ADMISION_TEMPORAL      → vehículo extranjero admitido temporalmente
    estado           VARCHAR(30)  NOT NULL DEFAULT 'EN_TERRITORIO_NACIONAL',

    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);