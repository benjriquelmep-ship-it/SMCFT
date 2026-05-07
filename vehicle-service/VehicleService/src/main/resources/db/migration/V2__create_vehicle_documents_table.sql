-- Documentos asociados a cada vehículo
-- Relación: muchos documentos pertenecen a un vehículo
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE vehicle_documents (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Clave foránea hacia la tabla vehicles
    -- Todo documento debe pertenecer a un vehículo existente
    vehicle_id        BIGINT       NOT NULL,

    -- Tipo de documento aduanero
    -- PERMISO_CIRCULACION, SEGURO_OBLIGATORIO, REVISION_TECNICA
    tipo              VARCHAR(50)  NOT NULL,

    -- Número único del documento
    numero            VARCHAR(50)  NOT NULL UNIQUE,

    -- Fecha de vencimiento del documento
    fecha_vencimiento DATE         NOT NULL,

    -- Si el documento está vigente o vencido
    vigente           BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    -- Definición de la clave foránea con integridad referencial
    -- ON DELETE CASCADE → si se elimina el vehículo, sus documentos también
    CONSTRAINT fk_vehicle_documents
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(id)
        ON DELETE CASCADE
);