# Логическая структура базы данных

## Общая схема связей

```
users ──< user_roles >── roles
  │                          │
  │                    permissions ──< role_permissions >── roles
  │
  ├──< payment_methods
  │       │
  │       └──< transactions
  │
  └──< user_sessions

books ──< book_authors >── authors
  │
  └──< book_categories >── categories
```

---

## 1. `users` — пользователи системы

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Уникальный идентификатор |
| email | varchar(255) UNIQUE | Email для входа и уведомлений |
| username | varchar(100) UNIQUE | Логин пользователя |
| password_hash | varchar(255) | BCrypt-хеш пароля |
| first_name / last_name | varchar(100) | Имя и фамилия |
| phone | varchar(50) | Телефон (опционально) |
| is_active | boolean | Флаг активности (soft-delete/блокировка) |
| is_email_verified | boolean | Подтверждён ли email |
| created_at / updated_at | timestamp | Аудит времени |

**Зачем:** Центральная таблица. Все остальные модули привязаны к пользователю. Флаг `is_active` позволяет заблокировать пользователя без удаления данных.

---

## 2. `roles` — роли доступа

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| name | varchar(50) UNIQUE | Название роли (`ROLE_ADMIN`, `ROLE_USER`) |
| description | varchar(255) | Описание |

**Зачем:** RBAC (Role-Based Access Control). Роли назначаются пользователям и проверяются Spring Security через `@PreAuthorize("hasRole('ROLE_ADMIN')")`.

---

## 3. `user_roles` — связь пользователей с ролями (Many-to-Many)

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор связи |
| user_id | bigint FK → users(id) | Пользователь |
| role_id | bigint FK → roles(id) | Роль |
| UNIQUE (user_id, role_id) | | Одна роль не может быть назначена дважды |

**Зачем:** Пользователь может иметь несколько ролей (например, `ROLE_ADMIN` + `ROLE_USER`). Join-таблица с уникальным constraint предотвращает дубликаты.

---

## 4. `permissions` — права (зарезервировано)

| Поле | Тип |
|---|---|
| id | bigserial PK |
| name | varchar(100) UNIQUE |

**Зачем:** Заготовка для гранулярного управления доступом (например, `BOOK_CREATE`, `USER_DELETE`). Пока не используется в коде.

---

## 5. `role_permissions` — связь ролей с правами (зарезервировано)

| Поле | Тип |
|---|---|
| role_id | bigint PK/FK → roles(id) |
| permission_id | bigint PK/FK → permissions(id) |

**Зачем:** Заготовка для расширения RBAC до ABAC. Composite primary key.

---

## 6. `books` — каталог книг

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| title | varchar(255) | Название |
| description | text | Описание |
| isbn | varchar(50) UNIQUE | Международный ISBN |
| price | numeric(10,2) | Цена продажи |
| rental_price | numeric(10,2) | Цена аренды |
| deposit_amount | numeric(10,2) | Залог (для аренды) |
| stock_count | int | Количество на складе |
| published_year | int | Год издания |
| cover_url | varchar(500) | Ссылка на обложку |
| is_active | boolean | Soft-delete (книга скрыта, но не удалена) |
| created_at / updated_at | timestamp | Аудит |

**Зачем:** Основной каталог. `is_active` позволяет скрыть книгу, сохраняя историю заказов/аренды. `price` + `rental_price` + `deposit_amount` дают гибкость: книгу можно продать, арендовать или и то, и другое.

---

## 7. `authors` — авторы

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| full_name | varchar(255) | Полное имя |
| bio | text | Биография |
| created_at | timestamp | Аудит |

**Зачем:** Авторы вынесены отдельно, а не хранятся строкой в `books`, потому что один автор может написать много книг.

---

## 8. `book_authors` — связь книг с авторами (Many-to-Many)

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| book_id | bigint FK → books(id) | Книга |
| author_id | bigint FK → authors(id) | Автор |
| author_role | varchar(50) enum(MAIN_AUTHOR, CO_AUTHOR) | Роль автора |
| author_order | int | Порядок отображения |
| created_at | timestamp | Аудит |

**Зачем:** Одна книга может иметь несколько авторов (например, соавторы). Поля `author_role` и `author_order` позволяют корректно отображать: "Автор1 (главный), Автор2 (соавтор)".

---

## 9. `categories` — категории

| Поле | Тип |
|---|---|
| id | bigserial PK |
| name | varchar(100) UNIQUE |
| created_at | timestamp |

**Зачем:** Рубрикация книг (Programming, Java, Spring, Backend). Одна книга может быть в нескольких категориях.

---

## 10. `book_categories` — связь книг с категориями (Many-to-Many)

| Поле | Тип |
|---|---|
| book_id | bigint PK/FK → books(id) |
| category_id | bigint PK/FK → categories(id) |

**Зачем:** Composite primary key без суррогатного id. Книга может принадлежать нескольким категориям одновременно.

---

## 11. `payment_methods` — способы оплаты пользователя

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| user_id | bigint FK → users(id) | Владелец |
| type | varchar(50) | CARD, PAYPAL, etc. |
| provider | varchar(50) | VISA, MASTERCARD |
| account_number | varchar(100) | "Маскированный" номер карты |
| expiry_date | varchar(10) | Срок действия |
| is_default | boolean | Способ по умолчанию |
| created_at | timestamp | Аудит |

**Зачем:** Один пользователь может иметь несколько способов оплаты. `is_default` — для быстрого выбора при оплате.

---

## 12. `transactions` — финансовые транзакции

| Поле | Тип | Назначение |
|---|---|---|
| id | bigserial PK | Идентификатор |
| user_id | bigint FK → users(id) | Плательщик |
| payment_method_id | bigint FK → payment_methods(id) | Способ оплаты |
| amount | numeric(10,2) | Сумма |
| currency | varchar(10) | Валюта (RUB, USD) |
| status | varchar(50) | SUCCESS / FAILED |
| created_at | timestamp | Дата |

**Зачем:** Аудит всех финансовых операций. Связь с `payment_methods` позволяет узнать, какой картой платили.

---

## 13. `user_sessions` — сессии (зарезервировано)

| Поле | Тип |
|---|---|
| id | bigserial PK |
| user_id | bigint FK → users(id) |
| token | varchar(500) |
| expires_at | timestamp |
| created_at | timestamp |

**Зачем:** Заготовка для хранения сессий (если потребуется отойти от stateless JWT или логировать активные сессии). Сейчас refresh-токены хранятся в Redis, не в этой таблице.

---

## Итог: типы связей

| Тип связи | Где используется |
|---|---|
| OneToMany (User → PaymentMethod) | У пользователя много карт |
| OneToMany (User → Transaction) | У пользователя много платежей |
| OneToMany (User → UserRole) | У пользователя много ролей |
| OneToMany (Role → UserRole) | Роль назначается многим пользователям |
| ManyToMany (User ↔ Role через UserRole) | Связь с атрибутами |
| ManyToMany (Book ↔ Author через BookAuthor) | Связь с атрибутами (роль, порядок) |
| ManyToMany (Book ↔ Category через BookCategory) | Простая связь, composite PK |
| ManyToOne (Transaction → PaymentMethod) | Транзакция совершена через конкретный способ оплаты |
