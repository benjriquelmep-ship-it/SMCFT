-- Items revisados en cada inspección sanitaria
-- Relación: muchos items pertenecen a una inspección
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE sanitary_items (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia sanitary_inspections
    inspection_id  BIGINT       NOT NULL,

    -- Descripción del item inspeccionado
    -- Ej: "Limpieza del vehículo", "Productos alimenticios",
    --     "Animales vivos", "Plantas"
    descripcion    VARCHAR(200) NOT NULL,

    -- Resultado de este item específico
    -- APROBADO, RECHAZADO, NO_APLICA
    resultado_item VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',

    -- Observaciones específicas del item
    observaciones  VARCHAR(300) NULL,

    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_sanitary_items
        FOREIGN KEY (inspection_id)
        REFERENCES sanitary_inspections(id)
        ON DELETE CASCADE
);