-- =========================
-- V6: Добавление таблицы жанров (genres) и связи книг с жанрами (book_genres)
-- =========================
-- Жанры отличаются от категорий: категории — рубрикация (Programming, Java),
-- жанры — литературные жанры (Fantasy, Detective, Sci-Fi).
-- Many-to-Many связь через отдельную таблицу для быстрой фильтрации.

-- 1. Таблица жанров
create table genres (
    id bigserial primary key,
    name varchar(100) not null unique,
    description text,
    created_at timestamp default now()
);

-- 2. Таблица связи книг с жанрами (Many-to-Many) с суррогатным ключом
create table book_genres (
    id bigserial primary key,
    book_id bigint not null,
    genre_id bigint not null,

    constraint uq_book_genres_book_genre
        unique (book_id, genre_id),

    constraint fk_book_genres_book
        foreign key (book_id) references books(id) on delete cascade,

    constraint fk_book_genres_genre
        foreign key (genre_id) references genres(id) on delete cascade
);

-- 3. Индексы для ускорения JOIN-запросов при фильтрации
create index idx_book_genres_genre_id on book_genres(genre_id);
create index idx_book_genres_book_id on book_genres(book_id);

-- 4. Индекс для поиска жанров по названию
create index idx_genres_name on genres(name);

-- 5. Начальное заполнение популярными жанрами (seed)
insert into genres (name, description, created_at) values
    ('Fantasy', 'Произведения с вымышленными мирами, магией и сверхъестественными существами', now()),
    ('Science Fiction', 'Научная фантастика: космос, технологии, будущее', now()),
    ('Detective', 'Детективы и криминальные истории с расследованиями', now()),
    ('Romance', 'Любовные истории и романтические отношения', now()),
    ('Thriller', 'Остросюжетные произведения с напряжённым сюжетом', now()),
    ('Horror', 'Ужасы: произведения, вызывающие страх и тревогу', now()),
    ('Adventure', 'Приключенческие истории с путешествиями и опасностями', now()),
    ('Mystery', 'Мистика и загадки, которые нужно разгадать', now()),
    ('Historical Fiction', 'Историческая проза: события в прошлом с художественным вымыслом', now()),
    ('Non-Fiction', 'Научно-популярная и документальная литература', now()),
    ('Biography', 'Биографии реальных людей', now()),
    ('Self-Help', 'Книги по саморазвитию и личностному росту', now()),
    ('Philosophy', 'Философские произведения и размышления', now()),
    ('Poetry', 'Поэзия и стихотворные произведения', now()),
    ('Drama', 'Драматические произведения и пьесы', now()),
    ('Comedy', 'Комедийные и юмористические произведения', now()),
    ('Dystopia', 'Дистопии: мрачное будущее, тоталитарные общества', now()),
    ('Young Adult', 'Молодёжная литература для подростков', now()),
    ('Children', 'Детская литература', now()),
    ('Classic', 'Классическая литература', now())
on conflict (name) do nothing;

-- 6. Комментарии к таблицам
comment on table genres is 'Литературные жанры книг (Fantasy, Detective, Sci-Fi и т.д.)';
comment on table book_genres is 'Связь книг с жанрами (Many-to-Many)';
comment on column genres.name is 'Название жанра';
comment on column genres.description is 'Описание жанра';
