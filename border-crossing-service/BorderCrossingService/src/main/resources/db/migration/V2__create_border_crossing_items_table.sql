-- V2__create_border_crossing_items_table.sql
-- Items de equipaje declarados en cada cruce fronterizo
-- Relación: muchos items pertenecen a un cruce
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE border_crossing_items (
    id                  BIGINT        AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia border_crossings
    -- Todo item debe pertenecer a un cruce existente
    border_crossing_id  BIGINT        NOT NULL,

    -- ID de la categoría del item — viene de Item Category Service
    -- No FK directa — desacoplado de Item Category Service
    categoria_id        BIGINT        NOT NULL,

    -- Descripción del item declarado por el viajero
    descripcion         VARCHAR(200)  NOT NULL,

    -- Cantidad del item
    cantidad            INT           NOT NULL DEFAULT 1,

    -- Valor declarado en dólares
    valor_usd           DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- Si el fiscalizador aprobó este item
    aprobado            BOOLEAN       NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    -- Si se elimina el cruce sus items también se eliminan
    CONSTRAINT fk_crossing_items
        FOREIGN KEY (border_crossing_id)
        REFERENCES border_crossings(id)
        ON DELETE CASCADE
);