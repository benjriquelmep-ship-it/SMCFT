-- V2__create_entry_items_table.sql
-- Items declarados en cada ingreso al país
-- Relación: muchos items pertenecen a un ingreso
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE entry_items (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia entries
    -- Todo item debe pertenecer a un ingreso existente
    entry_id    BIGINT        NOT NULL,

    -- Descripción del item declarado
    descripcion VARCHAR(200)  NOT NULL,

    -- Cantidad del item
    cantidad    INT           NOT NULL DEFAULT 1,

    -- Valor declarado en dólares
    valor_usd   DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- Si el item fue aprobado por el fiscalizador
    aprobado    BOOLEAN       NOT NULL DEFAULT FALSE,

    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    -- Si se elimina el ingreso sus items también se eliminan
    CONSTRAINT fk_entry_items
        FOREIGN KEY (entry_id)
        REFERENCES entries(id)
        ON DELETE CASCADE
);