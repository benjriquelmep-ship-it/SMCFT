-- Esta tabla guarda los tokens invalidados cuando el usuario cierra sesión
-- Le da sentido propio a la base de datos separada del Auth Service

CREATE TABLE token_blacklist (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- TEXT porque los tokens JWT son muy largos para VARCHAR
    token         TEXT         NOT NULL,

    -- Email del usuario que cerró sesión — para auditoría
    email         VARCHAR(150) NOT NULL,

    -- Fecha y hora exacta del cierre de sesión
    invalidado_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);