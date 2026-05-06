-- Registra cada intento de login en el sistema
-- Permite detectar ataques de fuerza bruta
-- Tiene relación con token_blacklist a través de token_blacklist_id
-- Cumple con IE 2.2.3 — relaciones entre entidades

CREATE TABLE login_attempts (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Email con el que se intentó hacer login
    email               VARCHAR(150) NOT NULL,

    -- true = login exitoso, false = login fallido
    exitoso             BOOLEAN      NOT NULL,

    -- IP desde donde se intentó el login
    ip_address          VARCHAR(45),

    -- Fecha y hora exacta del intento
    intento_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    -- FK opcional hacia token_blacklist
    -- Se llena cuando el usuario hace logout después de un login exitoso
    -- nullable porque no todos los intentos tienen un logout asociado
    token_blacklist_id  BIGINT       NULL,

    -- Definición de la clave foránea con integridad referencial
    -- ON DELETE SET NULL → si se borra el token, el intento queda sin referencia
    CONSTRAINT fk_login_attempt_blacklist
        FOREIGN KEY (token_blacklist_id)
        REFERENCES token_blacklist(id)
        ON DELETE SET NULL
);