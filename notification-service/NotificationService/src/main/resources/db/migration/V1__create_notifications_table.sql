-- V1__create_notifications_table.sql
-- Registra las notificaciones del sistema fronterizo

CREATE TABLE notifications (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- Título de la notificación
    titulo            VARCHAR(200) NOT NULL,

    -- Mensaje de la notificación
    mensaje           VARCHAR(500) NOT NULL,

    -- Tipo de notificación
    -- ALERTA_DEADLINE, ALERTA_URGENTE, ALERTA_VENCIDO, INFORMATIVA
    tipo              VARCHAR(50)  NOT NULL,

    -- ID de la alerta de deadline que originó esta notificación
    -- null si es notificación manual
    deadline_alert_id BIGINT       NULL,

    -- Estado de la notificación
    -- PENDIENTE, ENVIADA, ERROR
    estado            VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',

    -- Fecha de creación
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,

    -- Fecha de envío
    enviada_at        DATETIME     NULL
);