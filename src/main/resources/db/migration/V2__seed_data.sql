-- =========================
-- AUTHORS
-- =========================
insert into authors (id, full_name, bio, created_at) values
                                                         (1, 'Robert C. Martin', 'Clean Code author', now()),
                                                         (2, 'Joshua Bloch', 'Effective Java author', now()),
                                                         (3, 'Craig Walls', 'Spring expert', now()),
                                                         (4, 'Martin Fowler', 'Refactoring and architecture expert', now());

-- =========================
-- CATEGORIES
-- =========================
insert into categories (id, name, created_at) values
                                                  (1, 'Programming', now()),
                                                  (2, 'Java', now()),
                                                  (3, 'Spring', now()),
                                                  (4, 'Backend', now()),
                                                  (5, 'Software Engineering', now());

-- =========================
-- BOOKS
-- =========================
insert into books (
    id, title, description, isbn,
    price, rental_price, deposit_amount,
    stock_count, published_year, cover_url,
    is_active, created_at, updated_at
) values
      (1, 'Clean Code', 'A Handbook of Agile Software Craftsmanship', '9780132350884',
       120.00, 20.00, 50.00,
       5, 2008, null,
       true, now(), now()),

      (2, 'Effective Java', 'Best practices for Java platform', '9780134685991',
       150.00, 25.00, 60.00,
       3, 2018, null,
       true, now(), now()),

      (3, 'Spring in Action', 'Guide to Spring Framework', '9781617294945',
       135.00, 22.00, 55.00,
       4, 2020, null,
       true, now(), now()),

      (4, 'Refactoring', 'Improving the design of existing code', '9780201485677',
       140.00, 24.00, 55.00,
       2, 1999, null,
       true, now(), now());

-- =========================
-- BOOK_AUTHORS (many-to-many)
-- =========================
insert into book_authors (
    id, book_id, author_id, author_role, author_order, created_at
) values
      (1, 1, 1, 'MAIN_AUTHOR', 1, now()),
      (2, 2, 2, 'MAIN_AUTHOR', 1, now()),
      (3, 3, 3, 'MAIN_AUTHOR', 1, now()),
      (4, 4, 4, 'MAIN_AUTHOR', 1, now());

-- пример книги с несколькими авторами
insert into book_authors (
    id, book_id, author_id, author_role, author_order, created_at
) values
    (5, 3, 1, 'CO_AUTHOR', 2, now());

-- =========================
-- BOOK_CATEGORIES (many-to-many)
-- =========================
insert into book_categories (book_id, category_id) values
-- Clean Code
(1, 1),
(1, 5),

-- Effective Java
(2, 1),
(2, 2),
(2, 5),

-- Spring in Action
(3, 1),
(3, 2),
(3, 3),
(3, 4),

-- Refactoring
(4, 1),
(4, 5);

-- =========================
-- ДОПОЛНИТЕЛЬНЫЕ КЕЙСЫ
-- =========================

-- книга без наличия (для теста логики stock)
insert into books (
    id, title, description, isbn,
    price, rental_price, deposit_amount,
    stock_count, published_year,
    is_active, created_at, updated_at
) values
    (5, 'Domain-Driven Design', 'DDD classic book', '9780321125217',
     160.00, 30.00, 70.00,
     0, 2003,
     true, now(), now());

insert into book_authors (id, book_id, author_id, author_role, author_order, created_at)
values (6, 5, 4, 'MAIN_AUTHOR', 1, now());

insert into book_categories (book_id, category_id)
values (5, 5);