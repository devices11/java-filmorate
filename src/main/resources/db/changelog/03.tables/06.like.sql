--liquibase formatted sql

--changeset devices11:create-like
CREATE TABLE IF NOT EXISTS filmorate."like" (
                               film_id integer NOT NULL,
                               user_id integer NOT NULL
);

comment on table filmorate."like" is 'Лайки фильмов';
comment on column filmorate."like".film_id is 'Идентификатор фильма';
comment on column filmorate."like".user_id is 'Идентификатор пользователя';
--rollback DROP TABLE filmorate.like;

--changeset devices11:create-like-fk
ALTER TABLE filmorate."like" ADD CONSTRAINT like_film_fk FOREIGN KEY (film_id) REFERENCES filmorate.film(film_id);
ALTER TABLE filmorate."like" ADD CONSTRAINT like_user_fk FOREIGN KEY (user_id) REFERENCES filmorate."user"(user_id);
--rollback DROP TABLE filmorate.like;

