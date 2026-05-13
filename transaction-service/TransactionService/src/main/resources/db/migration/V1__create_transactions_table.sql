-- Registra las transacciones financieras del sistema fronterizo
-- Una transacción = pago o cobro relacionado con un cruce o admisión

CREATE TABLE transactions (
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,

    -- RUT del usuario que realiza la transacción
    rut_usuario      VARCHAR(12)   NOT NULL,

    -- Tipo de transacción
    -- PAGO_MULTA        → pago de multa por incumplimiento
    -- PAGO_TASA         → pago de tasa de ingreso
    -- DEVOLUCION        → devolución de dinero
    -- COBRO_SERVICIO    → cobro por servicio fronterizo
    tipo             VARCHAR(50)   NOT NULL,

    -- Monto total de la transacción en pesos chilenos
    monto_total      DECIMAL(12,2) NOT NULL,

    -- Estado de la transacción
    -- PENDIENTE  → en proceso de pago
    -- COMPLETADA → pago confirmado
    -- RECHAZADA  → pago rechazado
    -- ANULADA    → transacción anulada
    estado           VARCHAR(30)   NOT NULL DEFAULT 'PENDIENTE',

    -- Descripción de la transacción
    descripcion      VARCHAR(500)  NOT NULL,

    -- Referencia externa — número de comprobante o folio
    referencia       VARCHAR(100)  NULL,

    -- Fecha de la transacción
    created_at       DATETIME      DEFAULT CURRENT_TIMESTAMP,

    -- Fecha de completación
    completada_at    DATETIME      NULL
);