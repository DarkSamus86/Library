-- =========================
-- USERS + ROLES
-- =========================

create table users (
                       id bigserial primary key,
                       email varchar(255) not null unique,
                       username varchar(100) not null unique,
                       password_hash varchar(255) not null,
                       first_name varchar(100),
                       last_name varchar(100),
                       phone varchar(50),
                       is_active boolean,
                       is_email_verified boolean,
                       created_at timestamp,
                       updated_at timestamp
);

create table roles (
                       id bigserial primary key,
                       name varchar(50) not null unique,
                       description varchar(255)
);

create table user_roles (
                            user_id bigint not null,
                            role_id bigint not null,

                            primary key (user_id, role_id),

                            constraint fk_user_roles_user
                                foreign key (user_id) references users(id) on delete cascade,

                            constraint fk_user_roles_role
                                foreign key (role_id) references roles(id) on delete cascade
);

-- =========================
-- PERMISSIONS
-- =========================

create table permissions (
                             id bigserial primary key,
                             name varchar(100) not null unique
);

create table role_permissions (
                                  role_id bigint not null,
                                  permission_id bigint not null,

                                  primary key (role_id, permission_id),

                                  constraint fk_role_permissions_role
                                      foreign key (role_id) references roles(id) on delete cascade,

                                  constraint fk_role_permissions_permission
                                      foreign key (permission_id) references permissions(id) on delete cascade
);

-- =========================
-- BOOK MODULE
-- =========================

create table authors (
                         id bigserial primary key,
                         full_name varchar(255),
                         bio text,
                         created_at timestamp
);

create table books (
                       id bigserial primary key,
                       title varchar(255),
                       description text,
                       isbn varchar(50),
                       price numeric(10,2),
                       rental_price numeric(10,2),
                       deposit_amount numeric(10,2),
                       stock_count int,
                       published_year int,
                       cover_url varchar(500),
                       is_active boolean,
                       created_at timestamp,
                       updated_at timestamp
);

create table categories (
                            id bigserial primary key,
                            name varchar(100),
                            created_at timestamp
);

create table book_authors (
                              id bigserial primary key,
                              book_id bigint,
                              author_id bigint,
                              author_role varchar(50),
                              author_order int,
                              created_at timestamp,

                              constraint fk_book_authors_book
                                  foreign key (book_id) references books(id) on delete cascade,

                              constraint fk_book_authors_author
                                  foreign key (author_id) references authors(id) on delete cascade
);

create table book_categories (
                                 book_id bigint,
                                 category_id bigint,

                                 primary key (book_id, category_id),

                                 constraint fk_book_categories_book
                                     foreign key (book_id) references books(id) on delete cascade,

                                 constraint fk_book_categories_category
                                     foreign key (category_id) references categories(id) on delete cascade
);

-- =========================
-- PAYMENT MODULE
-- =========================

create table payment_methods (
                                 id bigserial primary key,
                                 user_id bigint,
                                 type varchar(50),
                                 provider varchar(50),
                                 account_number varchar(100),
                                 expiry_date varchar(10),
                                 is_default boolean,
                                 created_at timestamp,

                                 constraint fk_payment_methods_user
                                     foreign key (user_id) references users(id) on delete cascade
);

create table transactions (
                              id bigserial primary key,
                              user_id bigint,
                              amount numeric(10,2),
                              currency varchar(10),
                              status varchar(50),
                              payment_method_id bigint,
                              created_at timestamp,

                              constraint fk_transactions_user
                                  foreign key (user_id) references users(id),

                              constraint fk_transactions_payment
                                  foreign key (payment_method_id) references payment_methods(id)
);

-- =========================
-- SESSIONS
-- =========================

create table user_sessions (
                               id bigserial primary key,
                               user_id bigint,
                               token varchar(500),
                               expires_at timestamp,
                               created_at timestamp,

                               constraint fk_user_sessions_user
                                   foreign key (user_id) references users(id) on delete cascade
);