-- =========================
-- ROLES
-- =========================
insert into roles (name, description) values
                                          ('ROLE_USER', 'Default user role'),
                                          ('ROLE_ADMIN', 'Administrator role');

-- =========================
-- USERS
-- =========================
-- password: 123456 (bcrypt hash)
-- id генерируется автоматически
insert into users (
    email, username, password_hash,
    first_name, last_name,
    is_active, is_email_verified,
    created_at, updated_at
) values
      (
          'admin@library.com',
          'admin',
          '$2a$10$3mlp6cmwpN8fQJNpanON8.IEFx49Q4wjZJcJQZYsOMUVQT2irjt0S',
          'Admin',
          'User',
          true,
          true,
          now(),
          now()
      ),
      (
          'user@library.com',
          'user',
          '$2a$10$3mlp6cmwpN8fQJNpanON8.IEFx49Q4wjZJcJQZYsOMUVQT2irjt0S',
          'Regular',
          'User',
          true,
          true,
          now(),
          now()
      );

-- =========================
-- USER ROLES
-- =========================
-- id генерируется автоматически, указываем только user_id и role_id
insert into user_roles (user_id, role_id) values
                                              (1, 2), -- admin → ROLE_ADMIN
                                              (2, 1); -- user → ROLE_USER
