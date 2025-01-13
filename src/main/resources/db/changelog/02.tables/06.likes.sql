--liquibase formatted sql

--changeset devices11:create-like
CREATE TABLE IF NOT EXISTS filmorate.likes (
                               film_id integer NOT NULL,
                               user_id integer NOT NULL
);

comment on table filmorate.likes is 'Лайки фильмов';
comment on column filmorate.likes.film_id is 'Идентификатор фильма';
comment on column filmorate.likes.user_id is 'Идентификатор пользователя';
--rollback DROP TABLE filmorate.like;

--changeset devices11:create-like-fk
ALTER TABLE filmorate.likes ADD CONSTRAINT like_film_fk FOREIGN KEY (film_id) REFERENCES filmorate.films(film_id);
ALTER TABLE filmorate.likes ADD CONSTRAINT like_user_fk FOREIGN KEY (user_id) REFERENCES filmorate.users(user_id);
--rollback DROP TABLE filmorate.like;

