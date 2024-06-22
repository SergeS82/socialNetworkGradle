# Конфигурационные переменные
DB_PORT="5432"
DB_USER="admin"      # Замените на имя пользователя для вашей базы данных
DB_PASSWORD="root"   # Замените на пароль для вашей базы данных
# Создание базы данных socialnetwork
create_db_command="CREATE DATABASE socialnetwork;"
# Создание схемы sn
create_schema_command="CREATE SCHEMA sn;"
# Подключение к PostgreSQL и выполнение SQL-команд
echo "step1"
PGPASSWORD=$DB_PASSWORD psql -p $DB_PORT -U $DB_USER -d postgres -c "$create_db_command"
echo "step2"
PGPASSWORD=$DB_PASSWORD psql -p $DB_PORT -U $DB_USER -d socialnetwork -c "$create_schema_command"
echo "step3"
PGPASSWORD=$DB_PASSWORD psql -p $DB_PORT -U $DB_USER -d socialnetwork<<-EOSQL
CREATE USER super WITH PASSWORD 'super';
GRANT ALL PRIVILEGES ON DATABASE socialnetwork TO super;
-- Пользователи
CREATE TABLE sn.author (
	is_deleted bool NULL,
	sex bpchar(1) NULL,
	id bigserial NOT NULL,
	city varchar(255) NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	mail varchar(255) NULL,
	phone varchar(255) NULL,
	CONSTRAINT author_pkey PRIMARY KEY (id)
);
ALTER TABLE sn.author
ADD CONSTRAINT author_mail
CHECK (mail ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
-- Подписки
CREATE TABLE sn."subscription" (
	author int8 NULL,
	id bigserial NOT NULL,
	"subscription" int8 NULL,
	CONSTRAINT subscription_pkey PRIMARY KEY (id),
	CONSTRAINT subscription_fk1 FOREIGN KEY ("subscription") REFERENCES sn.author(id),
	CONSTRAINT subscription_fk2 FOREIGN KEY (author) REFERENCES sn.author(id)
);
-- Посты
CREATE TABLE sn.post (
	author int8 NULL,
	id bigserial NOT NULL,
	caption varchar(255) NULL,
	"text" varchar(255) NULL,
	CONSTRAINT post_pkey PRIMARY KEY (id),
	CONSTRAINT post_fk1 FOREIGN KEY (author) REFERENCES sn.author(id)
);
-- Комментарии
CREATE TABLE sn."comment" (
	author int8 NULL,
	creation_time timestamp(6) NULL,
	id bigserial NOT NULL,
	post int8 NULL,
	"text" varchar(255) NULL,
	CONSTRAINT comment_pkey PRIMARY KEY (id),
	CONSTRAINT comment_fk1 FOREIGN KEY (post) REFERENCES sn.post(id),
	CONSTRAINT comment_fk2 FOREIGN KEY (author) REFERENCES sn.author(id)
);
-- Лайки на постах
CREATE TABLE sn.likes_to_post (
	author int8 NULL,
	id bigserial NOT NULL,
	post int8 NULL,
	CONSTRAINT likes_to_post_pkey PRIMARY KEY (id),
	CONSTRAINT likes_to_post_fk1 FOREIGN KEY (post) REFERENCES sn.post(id),
	CONSTRAINT likes_to_post_fk2 FOREIGN KEY (author) REFERENCES sn.author(id)
);
-- Лайки на комментариях
CREATE TABLE sn.likes_to_comment (
	author int8 NULL,
	"comment" int8 NULL,
	id bigserial NOT NULL,
	CONSTRAINT likes_to_comment_pkey PRIMARY KEY (id),
	CONSTRAINT fkprk999ldaf682j2s9y49rgu8 FOREIGN KEY (author) REFERENCES sn.author(id),
	CONSTRAINT fkt0pdbl2383u99gt7bm9bhorkv FOREIGN KEY ("comment") REFERENCES sn."comment"(id)
);
EOSQL
echo "Database, schema, and table have been created successfully."
