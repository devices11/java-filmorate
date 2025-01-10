--liquibase formatted sql

--changeset devices11:create-user
create table if not exists filmorate."user"
(
    user_id  integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login    varchar,
    name     varchar,
    email    varchar,
    birthday date
);

comment on table filmorate."user" is 'Информация пользователя';
comment on column filmorate."user".user_id is 'Идентификатор пользователя';
comment on column filmorate."user".login is 'Логин пользователя';
comment on column filmorate."user".name is 'Имя пользователя';
comment on column filmorate."user".email is 'Электронная почта пользователя';
comment on column filmorate."user".birthday is 'Дата рождения пользователя';
--rollback DROP TABLE public.user;

--changeset devices11:create-user_login_idx-index
CREATE INDEX user_login_idx ON filmorate."user" (login, name, email);
--rollback DROP INDEX filmorate.user_login_idx;