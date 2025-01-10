--liquibase formatted sql

--changeset devices11:create-film_rating_enum
CREATE TYPE filmorate.film_rating_enum AS ENUM (
	'G',
	'PG',
	'PG-13',
	'R',
	'NC-17');
--rollback DROP TYPE filmorate."film_rating";