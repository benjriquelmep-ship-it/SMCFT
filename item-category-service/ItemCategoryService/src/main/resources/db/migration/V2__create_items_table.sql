-- Items específicos dentro de cada categoría
-- Relación: muchos items pertenecen a una categoría
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia item_categories
    -- Todo item debe pertenecer a una categoría existente
    categoria_id BIGINT       NOT NULL,

    -- Nombre del item específico
    -- Ej: "Laptop", "Tablet", "Smartphone"
    nombre       VARCHAR(100) NOT NULL,

    -- Descripción del item
    descripcion  VARCHAR(300) NOT NULL,

    -- Unidad de medida del item
    -- Ej: "unidad", "kg", "litro"
    unidad       VARCHAR(30)  NOT NULL DEFAULT 'unidad',

    -- Si el item está activo en el sistema
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    -- Si se elimina la categoría sus items también se eliminan
    CONSTRAINT fk_items_category
        FOREIGN KEY (categoria_id)
        REFERENCES item_categories(id)
        ON DELETE CASCADE
);
