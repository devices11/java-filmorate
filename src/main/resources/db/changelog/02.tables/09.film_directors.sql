--liquibase formatted sql

--changeset devices11:create-film_directors
CREATE TABLE IF NOT EXISTS filmorate.film_directors (
        film_id integer NOT NULL,
        director_id integer NOT NULL
);

comment on table filmorate.film_directors is 'Маппинг режиссеров к фильмам';
comment on column filmorate.film_directors.film_id is 'Идентификатор фильма';
comment on column filmorate.film_directors.director_id is 'Идентификатор режиссера';
--rollback DROP TABLE filmorate.film_directors;

--changeset devices11:create-film_directors-fk
ALTER TABLE filmorate.film_directors ADD CONSTRAINT film_directors_film_fk
    FOREIGN KEY (film_id) REFERENCES filmorate.films(film_id) ON DELETE CASCADE;
ALTER TABLE filmorate.film_directors ADD CONSTRAINT film_directors_directors_fk
    FOREIGN KEY (director_id) REFERENCES filmorate.directors(director_id) ON DELETE CASCADE;
--rollback DROP TABLE filmorate.film_directors;