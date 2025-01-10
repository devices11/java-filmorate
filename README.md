# java-filmorate


## DB sheme
![sheme](./doc/filmorate_db_scheme.png)

### Query

Создание бд, [скрипты](./src/main/resources/db/changelog)


GET /films/{id}
```
SELECT f.*, m."name" AS mpa_name
FROM filmorate.film f
JOIN filmorate.MPA m ON f.MPA_ID = m.id
WHERE f.film_id = ?
```

GET /films
```
SELECT f.*, m."name" AS mpa_name
FROM filmorate.film f
JOIN filmorate.MPA m ON f.MPA_ID = m.id
```

POST /films
```
INSERT INTO filmorate.film ("name", description, release_date, duration, mpa_id)
VALUES(?, ?, ?, ?, ?)
INSERT INTO filmorate.film_genre (genre_id, film_id)
VALUES(?, ?)
```
PUT /films
```
UPDATE filmorate.film
SET "name" = ?,
description = ?,
release_date = ?,
duration = ?,
mpa_id = ?
WHERE film_id = ?
```

PUT /films/{id}/like/{userId}
```
INSERT INTO filmorate."like" (film_id, user_id)
VALUES(?, ?)
```

DELETE /films/{id}/like/{userId}
```
DELETE FROM filmorate."like" WHERE film_id = ? and user_id = ?
```

GET /films/popular
```
SELECT l.FILM_ID FROM FILMORATE."like" l
GROUP BY l.FILM_ID
ORDER BY COUNT(l.FILM_ID) DESC
LIMIT ?
```

GET /users/{id}
```
SELECT * FROM filmorate."user" WHERE user_id = ?
```

GET /users
```
SELECT * FROM filmorate."user"
```

POST /users
```
INSERT INTO filmorate."user"(login, name, email, birthday)
VALUES (?, ?, ?, ?)
```

PUT /users
```
UPDATE filmorate."user"
SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?
```

GET /users/{id}/friends
```
SELECT u.*, f.CONFIRMED FROM FILMORATE."user" u
JOIN FILMORATE.FRIEND f ON u.USER_ID = f.FRIEND_ID
WHERE f.USER_ID = ?
```

PUT /users/{id}/friends/{friendId}
```
INSERT INTO filmorate.friend (confirmed, user_id, friend_id)
VALUES(?, ?, ?)
```

DELETE /users/{id}/friends/{friendId}
```
DELETE FROM FILMORATE.FRIEND WHERE USER_ID = ? AND FRIEND_ID = ?
```

GET /genres
```
SELECT * FROM filmorate.genre ORDER BY genre_id
```

GET /genres/{id}
```
SELECT * FROM filmorate.genre WHERE genre_id = ?
```

GET /mpa
```
SELECT * FROM filmorate.mpa ORDER BY id
```

GET /mpa/{id}
```
SELECT * FROM filmorate.mpa WHERE id = ?
```