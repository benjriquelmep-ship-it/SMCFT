-- Detalles de cada transacción
-- Relación: muchos detalles pertenecen a una transacción
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE transaction_details (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,

    -- FK hacia transactions
    transaction_id BIGINT        NOT NULL,

    -- Descripción del concepto cobrado
    concepto       VARCHAR(200)  NOT NULL,

    -- Monto de este concepto específico
    monto          DECIMAL(12,2) NOT NULL,

    -- Cantidad de unidades del concepto
    cantidad       INT           NOT NULL DEFAULT 1,

    -- Tipo de detalle
    -- COBRO     → monto a pagar
    -- DESCUENTO → descuento aplicado
    -- IMPUESTO  → impuesto aplicado
    tipo_detalle   VARCHAR(30)   NOT NULL DEFAULT 'COBRO',

    created_at     DATETIME      DEFAULT CURRENT_TIMESTAMP,

    -- Integridad referencial
    CONSTRAINT fk_transaction_details
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(id)
        ON DELETE CASCADE
);