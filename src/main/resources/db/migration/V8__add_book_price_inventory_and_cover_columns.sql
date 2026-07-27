-- Добавление полей цены, количества физических экземпляров и обложки книги.
-- IF NOT EXISTS сохраняет совместимость с базами, где поля уже были
-- переименованы миграцией V5.
ALTER TABLE books
    ADD COLUMN IF NOT EXISTS price_purchase NUMERIC(10, 2),
    ADD COLUMN IF NOT EXISTS price_rental NUMERIC(10, 2),
    ADD COLUMN IF NOT EXISTS physical_inventory INT,
    ADD COLUMN IF NOT EXISTS cover_image_url VARCHAR(500);
