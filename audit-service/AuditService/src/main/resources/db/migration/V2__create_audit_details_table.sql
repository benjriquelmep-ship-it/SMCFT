-- Detalles de cada auditoría realizada
-- Relación: muchos detalles pertenecen a una auditoría
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE audit_details (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia audits
    audit_id    BIGINT       NOT NULL,

    -- Acción auditada
    -- Ej: "LOGIN", "LOGOUT", "CREAR_USUARIO", "AUTORIZAR_CRUCE"
    accion      VARCHAR(100) NOT NULL,

    -- Descripción detallada de la acción
    descripcion VARCHAR(500) NOT NULL,

    -- RUT del usuario que realizó la acción
    rut_usuario VARCHAR(12)  NOT NULL,

    -- Resultado de la acción
    -- EXITOSO  → la acción se completó correctamente
    -- FALLIDO  → la acción falló
    -- SOSPECHO → la acción es sospechosa
    resultado   VARCHAR(30)  NOT NULL DEFAULT 'EXITOSO',

    -- Dirección IP desde donde se realizó la acción
    ip_address  VARCHAR(45)  NULL,

    -- Fecha y hora exacta de la acción
    fecha_accion DATETIME    NOT NULL,

    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_audit_details
        FOREIGN KEY (audit_id)
        REFERENCES audits(id)
        ON DELETE CASCADE
);