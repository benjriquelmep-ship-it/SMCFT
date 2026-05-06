-- Flyway ejecuta esto automáticamente al iniciar el servicio

CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut        VARCHAR(12)  NOT NULL UNIQUE,   -- RUT chileno (ej: 12345678-9)
    nombre     VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    rol        VARCHAR(50)  NOT NULL,           -- ADMINISTRADOR, FISCALIZADOR, VIAJERO
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);