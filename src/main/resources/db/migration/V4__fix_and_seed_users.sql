-- V4__fix_and_seed_users.sql

-- =========================
-- FIX user_roles table
-- =========================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_roles' AND column_name = 'id'
    ) THEN
ALTER TABLE user_roles DROP CONSTRAINT user_roles_pkey;
ALTER TABLE user_roles ADD COLUMN id bigserial PRIMARY KEY;
ALTER TABLE user_roles ADD CONSTRAINT uq_user_roles_user_role
    UNIQUE (user_id, role_id);
END IF;
END $$;

-- =========================
-- SEED USERS
-- =========================
INSERT INTO users (
    email, username, password_hash,
    first_name, last_name, phone,
    is_active, is_email_verified,
    created_at, updated_at
)
VALUES
    (
        'admin@library.com', 'admin',
        '$2a$12$WStoW8dOl0v0SB4NpKHGrOWoDtHHvEBkMQSQbzt8MprNmvCCpeuY.',
        'Admin', 'User', null,
        true, true, now(), now()
    ),
    (
        'user@library.com', 'testuser',
        '$2a$12$WStoW8dOl0v0SB4NpKHGrOWoDtHHvEBkMQSQbzt8MprNmvCCpeuY.',
        'Test', 'User', null,
        true, true, now(), now()
    )
    ON CONFLICT (username) DO NOTHING;

-- =========================
-- ASSIGN ROLES
-- =========================
INSERT INTO user_roles (user_id, role_id)
VALUES
    (
        (SELECT id FROM users WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
    ),
    (
        (SELECT id FROM users WHERE username = 'testuser'),
        (SELECT id FROM roles WHERE name = 'ROLE_USER')
    )
    ON CONFLICT DO NOTHING;