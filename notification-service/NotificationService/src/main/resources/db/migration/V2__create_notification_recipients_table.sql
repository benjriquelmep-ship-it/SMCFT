-- V2__create_notification_recipients_table.sql
-- Destinatarios de cada notificación
-- Relación: muchos destinatarios pertenecen a una notificación
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE notification_recipients (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia notifications
    notification_id  BIGINT       NOT NULL,

    -- RUT del destinatario
    rut_destinatario VARCHAR(12)  NOT NULL,

    -- Email del destinatario
    email            VARCHAR(150) NOT NULL,

    -- Nombre del destinatario
    nombre           VARCHAR(100) NOT NULL,

    -- Si el destinatario leyó la notificación
    leida            BOOLEAN      NOT NULL DEFAULT FALSE,

    -- Fecha en que leyó la notificación
    leida_at         DATETIME     NULL,

    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_notification_recipients
        FOREIGN KEY (notification_id)
        REFERENCES notifications(id)
        ON DELETE CASCADE
);