-- Registra las auditorías realizadas en el sistema fronterizo
-- Una auditoría = revisión de actividad de un usuario o proceso

CREATE TABLE audits (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- RUT del auditor que realiza la revisión
    rut_auditor      VARCHAR(12)  NOT NULL,

    -- Tipo de auditoría
    -- USUARIO          → auditoría de actividad de un usuario
    -- CRUCE_FRONTERIZO → auditoría de cruces fronterizos
    -- TRANSACCION      → auditoría de transacciones
    -- SISTEMA          → auditoría de operaciones del sistema
    tipo_auditoria   VARCHAR(50)  NOT NULL,

    -- Entidad auditada — nombre del microservicio o proceso
    -- Ej: "User Service", "Border Crossing Service"
    entidad          VARCHAR(100) NOT NULL,

    -- ID del registro auditado en la entidad correspondiente
    entidad_id       BIGINT       NULL,

    -- Descripción de la auditoría
    descripcion      VARCHAR(500) NOT NULL,

    -- Estado de la auditoría
    -- EN_PROCESO  → auditoría en curso
    -- COMPLETADA  → auditoría finalizada
    -- OBSERVACION → se encontraron irregularidades
    estado           VARCHAR(30)  NOT NULL DEFAULT 'EN_PROCESO',

    -- Fecha de inicio de la auditoría
    fecha_inicio     DATETIME     NOT NULL,

    -- Fecha de cierre de la auditoría
    fecha_cierre     DATETIME     NULL,

    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP
);