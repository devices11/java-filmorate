--liquibase formatted sql

--changeset devices11:insert-directors
INSERT INTO FILMORATE.DIRECTORS (director_id, "name")
    VALUES(1, 'Лучший'),
          (2, 'Петров'),
          (3, 'Смирнов'),
          (4, 'Пупкин');
--rollback DELETE FROM filmorate.DIRECTORS;

--changeset devices11:insert-users
INSERT INTO FILMORATE.USERS (LOGIN,NAME,EMAIL,BIRTHDAY) VALUES
        ('iAe2aSut2W','Carmen Harber','Ebba_Pagac0@hotmail.com','2003-05-14'),
        ('BwqnqDNBm8','Peter Wisozk','Wilbert_Kiehn@gmail.com','1990-12-09'),
        ('66jREhB2rg','Eula Kirlin','Abe_Ankunding9@gmail.com','1988-10-08');
--rollback DELETE FROM filmorate.USERS

--changeset devices11:insert-FILMS
INSERT INTO FILMORATE.FILMS (film_id, "name",DESCRIPTION,RELEASE_DATE,DURATION,MPA_ID) VALUES
      (200,'Доспехи бога 1','Фильм с Джеки','1986-12-01',100,5),
      (201,'Доспехи бога 2','Фильм с Джеки','1987-12-01',100,5),
      (202,'Доспехи бога 3','Фильм с Джеки','1988-12-01',100,5),
      (203,'Доспехи бога 3','Фильм с Джеки','1988-12-01',100,5);
--rollback DELETE FROM filmorate.FILMS

--changeset devices11:insert-LIKES
INSERT INTO FILMORATE.LIKES (FILM_ID,USER_ID) VALUES
      (200,1),
      (201,3),
      (201,2),
      (202,1),
      (202,2),
      (202,3);
--rollback DELETE FROM filmorate.LIKES

--changeset devices11:insert-FILM_DIRECTORS
INSERT INTO FILMORATE.FILM_DIRECTORS (FILM_ID,DIRECTOR_ID) VALUES
       (200,4),
       (201,4),
       (202,4);
--rollback DELETE FROM filmorate.FILM_DIRECTORS