--liquibase formatted sql

--changeset devices11:create-film_genre
CREATE TABLE IF NOT EXISTS filmorate.film_genre (
                                   genre_id integer NOT NULL,
                                   film_id integer NOT NULL
);

comment on table filmorate.film_genre is 'Маппинг жанров к фильмам';
comment on column filmorate.film_genre.genre_id is 'Идентификатор жанра';
comment on column filmorate.film_genre.film_id is 'Идентификатор фильма';
--rollback DROP TABLE filmorate.film_genre;

--changeset devices11:create-film_genre-fk
ALTER TABLE filmorate.film_genre ADD CONSTRAINT film_genre_film_fk FOREIGN KEY (film_id) REFERENCES filmorate.film(film_id);
ALTER TABLE filmorate.film_genre ADD CONSTRAINT film_genre_genre_fk FOREIGN KEY (genre_id) REFERENCES filmorate.genre(genre_id);
--rollback DROP TABLE filmorate.film_genre;

