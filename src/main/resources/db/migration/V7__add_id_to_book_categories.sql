-- V7: Добавить суррогатный id в book_categories
ALTER TABLE book_categories DROP CONSTRAINT book_categories_pkey;
ALTER TABLE book_categories ADD COLUMN id bigserial;
ALTER TABLE book_categories ADD PRIMARY KEY (id);
ALTER TABLE book_categories ADD CONSTRAINT uq_book_categories_book_category UNIQUE (book_id, category_id);