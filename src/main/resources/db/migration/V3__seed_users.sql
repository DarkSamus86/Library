-- =========================
-- ROLES
-- =========================
insert into roles (id, name, description) values
                                              (1, 'ROLE_USER', 'Default user role'),
                                              (2, 'ROLE_ADMIN', 'Administrator role');

-- =========================
-- USERS
-- =========================
-- password: 123456 (bcrypt)
-- hash сгенерирован заранее
insert into users (
    id, email, username, password_hash,
    first_name, last_name,
    is_active, is_email_verified,
    created_at, updated_at
) values
      (
          1,
          'admin@library.com',
          'admin',
          '$2a$10$7QJ5kQ1mYF1pYkZ6w0m8K.1j8G9wY6ZQeF0u8v8yJ8c6X3hKkP1lO',
          'Admin',
          'User',
          true,
          true,
          now(),
          now()
      ),
      (
          2,
          'user@library.com',
          'user',
          '$2a$10$7QJ5kQ1mYF1pYkZ6w0m8K.1j8G9wY6ZQeF0u8v8yJ8c6X3hKkP1lO',
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
insert into user_roles (id, user_id, role_id) values
                                                  (1, 1, 2), -- admin → ROLE_ADMIN
                                                  (2, 2, 1); -- user → ROLE_USER