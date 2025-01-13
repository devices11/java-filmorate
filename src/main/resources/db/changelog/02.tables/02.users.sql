--liquibase formatted sql

--changeset devices11:create-user
create table if not exists filmorate.users
(
    user_id  integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login    varchar,
    name     varchar,
    email    varchar,
    birthday date
);

comment on table filmorate.users is 'Информация пользователя';
comment on column filmorate.users.user_id is 'Идентификатор пользователя';
comment on column filmorate.users.login is 'Логин пользователя';
comment on column filmorate.users.name is 'Имя пользователя';
comment on column filmorate.users.email is 'Электронная почта пользователя';
comment on column filmorate.users.birthday is 'Дата рождения пользователя';
--rollback DROP TABLE public.user;

--changeset devices11:create-user_login_idx-index
CREATE INDEX user_login_idx ON filmorate.users (login, name, email);
--rollback DROP INDEX filmorate.user_login_idx;