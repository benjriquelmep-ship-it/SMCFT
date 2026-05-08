-- Categorías de equipaje que se pueden declarar en un cruce fronterizo
-- Border Crossing Service consulta este microservicio para validar categorías

CREATE TABLE item_categories (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Nombre de la categoría
    -- Ej: Electrónicos, Regalos, Mercancía Comercial
    nombre                VARCHAR(100) NOT NULL UNIQUE,

    -- Descripción detallada de la categoría
    descripcion           VARCHAR(500) NOT NULL,

    -- Si la categoría requiere declaración especial en aduanas
    -- true → requiere formulario adicional
    -- false → declaración estándar
    requiere_declaracion  BOOLEAN      NOT NULL DEFAULT FALSE,

    -- Límite de valor en USD permitido sin pago de impuestos
    -- null = sin límite establecido
    limite_valor_usd      DECIMAL(10,2) NULL,

    -- Si la categoría está activa en el sistema
    activo                BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at            TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
