-- V2__create_deadline_alerts_table.sql
-- Alertas generadas cuando un deadline está próximo a vencer
-- Relación: muchas alertas pertenecen a un deadline
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE deadline_alerts (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia deadlines
    deadline_id    BIGINT       NOT NULL,

    -- Mensaje de la alerta
    mensaje        VARCHAR(500) NOT NULL,

    -- Días restantes cuando se generó la alerta
    dias_restantes INT          NOT NULL,

    -- Tipo de alerta según urgencia
    -- AVISO, URGENTE, VENCIDO
    tipo_alerta    VARCHAR(30)  NOT NULL,

    -- Si la alerta ya fue enviada al Notification Service
    enviada        BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_deadline_alerts
        FOREIGN KEY (deadline_id)
        REFERENCES deadlines(id)
        ON DELETE CASCADE
);