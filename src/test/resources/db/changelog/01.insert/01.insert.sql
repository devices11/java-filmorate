--liquibase formatted sql

--changeset devices11:insert-directors
INSERT INTO FILMORATE.DIRECTORS (director_id, "name")
    VALUES(1, 'Лучший'),
          (2, 'Петров'),
          (3, 'Смирнов');
--rollback DELETE FROM filmorate.DIRECTORS;