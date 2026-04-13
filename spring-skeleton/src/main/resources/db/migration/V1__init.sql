-- ============================================================
--  V1__init.sql
--  Schema inicial do skeleton
-- ============================================================

-- ── Extensões ────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── Tabela: users ────────────────────────────────────────────
CREATE TABLE users (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,

    -- Auditoria
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    deleted_at   TIMESTAMP,             -- soft delete

    CONSTRAINT pk_users     PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email      ON users (email)      WHERE deleted_at IS NULL;
CREATE INDEX idx_users_active     ON users (is_active)  WHERE deleted_at IS NULL;
CREATE INDEX idx_users_deleted_at ON users (deleted_at);

-- ── Tabela: user_roles ───────────────────────────────────────
CREATE TABLE user_roles (
    user_id UUID         NOT NULL,
    role    VARCHAR(50)  NOT NULL,

    CONSTRAINT pk_user_roles   PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ── Seed: admin padrão ───────────────────────────────────────
-- Senha: Admin@123 (BCrypt – troque antes de subir em produção!)
INSERT INTO users (id, name, email, password, is_active, created_by)
VALUES (
    gen_random_uuid(),
    'Administrador',
    'admin@skeleton.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCkM/6fBRRvILyZQqhj5q7e',
    TRUE,
    'system'
);

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN'
FROM users
WHERE email = 'admin@skeleton.com';
