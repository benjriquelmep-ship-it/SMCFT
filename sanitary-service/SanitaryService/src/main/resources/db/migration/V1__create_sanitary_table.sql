-- Registra las inspecciones sanitarias de vehículos en la frontera
-- Una inspección sanitaria = revisión de estado sanitario del vehículo

CREATE TABLE sanitary_inspections (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- Patente del vehículo inspeccionado
    patente          VARCHAR(10)  NOT NULL,

    -- RUT del conductor
    rut_conductor    VARCHAR(12)  NOT NULL,

    -- RUT del inspector sanitario que realizó la revisión
    rut_inspector    VARCHAR(12)  NOT NULL,

    -- Paso fronterizo donde se realizó la inspección
    paso_fronterizo  VARCHAR(100) NOT NULL,

    -- Fecha y hora de la inspección
    fecha_inspeccion DATETIME     NOT NULL,

    -- Resultado de la inspección sanitaria
    -- APROBADO  → el vehículo pasó la inspección
    -- RECHAZADO → el vehículo no pasó la inspección
    -- PENDIENTE → en proceso de revisión
    resultado        VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',

    -- Observaciones del inspector
    observaciones    VARCHAR(500) NULL,

    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP
);