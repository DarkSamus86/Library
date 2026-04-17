-- =========================
-- AUTHORS
-- =========================
-- id генерируется автоматически через sequence
insert into authors (full_name, bio, created_at) values
                                                     ('Robert C. Martin', 'Clean Code author', now()),
                                                     ('Joshua Bloch', 'Effective Java author', now()),
                                                     ('Craig Walls', 'Spring expert', now()),
                                                     ('Martin Fowler', 'Refactoring and architecture expert', now());

-- =========================
-- CATEGORIES
-- =========================
insert into categories (name, created_at) values
                                              ('Programming', now()),
                                              ('Java', now()),
                                              ('Spring', now()),
                                              ('Backend', now()),
                                              ('Software Engineering', now());

-- =========================
-- BOOKS
-- =========================
insert into books (
    title, description, isbn,
    price, rental_price, deposit_amount,
    stock_count, published_year, cover_url,
    is_active, created_at, updated_at
) values
      ('Clean Code', 'A Handbook of Agile Software Craftsmanship', '9780132350884',
       120.00, 20.00, 50.00,
       5, 2008, null,
       true, now(), now()),

      ('Effective Java', 'Best practices for Java platform', '9780134685991',
       150.00, 25.00, 60.00,
       3, 2018, null,
       true, now(), now()),

      ('Spring in Action', 'Guide to Spring Framework', '9781617294945',
       135.00, 22.00, 55.00,
       4, 2020, null,
       true, now(), now()),

      ('Refactoring', 'Improving the design of existing code', '9780201485677',
       140.00, 24.00, 55.00,
       2, 1999, null,
       true, now(), now());

-- =========================
-- BOOK_AUTHORS (many-to-many)
-- =========================
-- Важно: вставляем только book_id и author_id, id генерируется автоматически
insert into book_authors (
    book_id, author_id, author_role, author_order, created_at
) values
      -- Clean Code → Robert C. Martin
      (1, 1, 'MAIN_AUTHOR', 1, now()),
      -- Effective Java → Joshua Bloch
      (2, 2, 'MAIN_AUTHOR', 1, now()),
      -- Spring in Action → Craig Walls
      (3, 3, 'MAIN_AUTHOR', 1, now()),
      -- Refactoring → Martin Fowler
      (4, 4, 'MAIN_AUTHOR', 1, now()),
      -- Spring in Action → Robert C. Martin (co-author)
      (3, 1, 'CO_AUTHOR', 2, now());

-- =========================
-- BOOK_CATEGORIES (many-to-many)
-- =========================
-- Здесь обычно нет id (composite key), но если есть - тоже не указываем
insert into book_categories (book_id, category_id) values
                                                       -- Clean Code
                                                       (1, 1), (1, 5),
                                                       -- Effective Java
                                                       (2, 1), (2, 2), (2, 5),
                                                       -- Spring in Action
                                                       (3, 1), (3, 2), (3, 3), (3, 4),
                                                       -- Refactoring
                                                       (4, 1), (4, 5);