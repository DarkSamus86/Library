-- =========================
-- V5: Разделение физических и цифровых копий в books
-- =========================
-- Добавляем поля для разделения physical/digital,
-- переименовываем старые поля, добавляем индексы и version для optimistic locking.

-- 1. Переименование существующих полей
ALTER TABLE books RENAME COLUMN price TO price_purchase;
ALTER TABLE books RENAME COLUMN rental_price TO price_rental;
ALTER TABLE books RENAME COLUMN stock_count TO physical_inventory;
ALTER TABLE books RENAME COLUMN cover_url TO cover_image_url;

-- 2. Добавление новых полей
ALTER TABLE books ADD COLUMN has_physical BOOLEAN DEFAULT false;
ALTER TABLE books ADD COLUMN has_digital BOOLEAN DEFAULT false;
ALTER TABLE books ADD COLUMN digital_licenses INT DEFAULT 0;
ALTER TABLE books ADD COLUMN is_available_for_rent BOOLEAN DEFAULT true;
ALTER TABLE books ADD COLUMN is_available_for_purchase BOOLEAN DEFAULT true;
ALTER TABLE books ADD COLUMN total_rentals_count INT DEFAULT 0;
ALTER TABLE books ADD COLUMN total_purchases_count INT DEFAULT 0;
ALTER TABLE books ADD COLUMN version INT DEFAULT 0;

-- 3. Заполнение has_physical и has_digital на основе существующих данных
-- Если physical_inventory > 0, значит книга имеет физическую версию
UPDATE books SET has_physical = true WHERE physical_inventory > 0;
-- По умолчанию считаем что все существующие книги имеют цифровую версию
-- (можно изменить позже вручную)
UPDATE books SET has_digital = true;
-- Безлимитные цифровые лицензии для существующих книг
UPDATE books SET digital_licenses = -1;

-- 4. Индексы для частых запросов
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_publication_year ON books(published_year);
CREATE INDEX idx_books_is_active ON books(is_active);
CREATE INDEX idx_books_has_physical ON books(has_physical);
CREATE INDEX idx_books_has_digital ON books(has_digital);
CREATE INDEX idx_books_physical_inventory ON books(physical_inventory);
CREATE INDEX idx_books_digital_licenses ON books(digital_licenses);
CREATE INDEX idx_books_is_available_for_rent ON books(is_available_for_rent);
CREATE INDEX idx_books_is_available_for_purchase ON books(is_available_for_purchase);

-- 5. Комментарии к полям (опционально, для документации в БД)
COMMENT ON COLUMN books.price_purchase IS 'Цена покупки книги (в тенге)';
COMMENT ON COLUMN books.price_rental IS 'Цена аренды за период (в тенге)';
COMMENT ON COLUMN books.deposit_amount IS 'Залог при аренде (в тенге)';
COMMENT ON COLUMN books.physical_inventory IS 'Количество бумажных копий на полке (0 = unavailable)';
COMMENT ON COLUMN books.digital_licenses IS 'Количество цифровых лицензий (-1 = безлимит)';
COMMENT ON COLUMN books.has_physical IS 'Доступна ли физическая версия';
COMMENT ON COLUMN books.has_digital IS 'Доступна ли цифровая версия';
COMMENT ON COLUMN books.is_available_for_rent IS 'Ручной тумблер доступности аренды';
COMMENT ON COLUMN books.is_available_for_purchase IS 'Ручной тумблер доступности покупки';
COMMENT ON COLUMN books.total_rentals_count IS 'Счётчик аренд для аналитики';
COMMENT ON COLUMN books.total_purchases_count IS 'Счётчик покупок для аналитики';
COMMENT ON COLUMN books.version IS 'Версия для optimistic locking (защита от race condition)';
