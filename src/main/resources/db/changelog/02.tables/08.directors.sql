--liquibase formatted sql

--changeset devices11:create-directors
CREATE TABLE IF NOT EXISTS filmorate.directors (
                                             director_id integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                             "name" varchar NOT NULL,
                                             CONSTRAINT director_pk PRIMARY KEY (director_id),
                                            CONSTRAINT director_unique UNIQUE ("name")
    );

comment on table filmorate.directors is 'Режиссер фильма';
comment on column filmorate.directors.director_id is 'Идентификатор режиссера';
comment on column filmorate.directors."name" is 'ФИО режиссера';
--rollback DROP TABLE filmorate.directors;