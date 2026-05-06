-- Tabla de historial de roles del usuario
-- Un usuario puede haber tenido varios roles a lo largo del tiempo
-- Relación: muchos roles pertenecen a un usuario (@ManyToOne)
-- Cumple con IE 2.2.3 — relaciones entre entidades con integridad referencial

CREATE TABLE user_roles (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Clave foránea que referencia a la tabla users
    -- Todo rol debe pertenecer a un usuario existente
    user_id      BIGINT      NOT NULL,

    -- Rol asignado en este momento histórico
    rol          VARCHAR(50) NOT NULL,

    -- Fecha y hora en que se asignó este rol
    asignado_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    -- Si este rol sigue siendo el activo del usuario
    -- activo = false significa que fue reemplazado por un nuevo rol
    activo       BOOLEAN     NOT NULL DEFAULT TRUE,

    -- Definición de la clave foránea con integridad referencial
    -- ON DELETE CASCADE: si se elimina el usuario, sus roles también se eliminan
    CONSTRAINT fk_user_roles
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);