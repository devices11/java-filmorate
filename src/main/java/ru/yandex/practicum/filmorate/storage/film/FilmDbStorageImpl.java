package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.*;

@Repository
public class FilmDbStorageImpl extends BaseStorage<Film> implements FilmDbStorage {
    private final FilmRowMapper filmRowMapper;

    private static final String FIND_ALL_QUERY = """
            SELECT f.*, m."name" AS mpa_name
            FROM filmorate.films f
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*, m."name" AS mpa_name
            FROM filmorate.films f
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            WHERE f.film_id = ?
            """;
    private static final String INSERT_FILM_QUERY = """
            INSERT INTO filmorate.films ("name", description, release_date, duration, mpa_id)
            VALUES(?, ?, ?, ?, ?)
            """;
    private static final String INSERT_FILM_GENRE_QUERY = """
            INSERT INTO filmorate.film_genres (genre_id, film_id)
            VALUES(?, ?)
            """;
    private static final String UPDATE_FILM_QUERY = """
            UPDATE filmorate.films
            SET "name" = ?,
                description = ?,
                release_date = ?,
                duration = ?,
                mpa_id = ?
            WHERE film_id = ?
            """;
    private static final String DELETE_FILM_QUERY = """
            DELETE FROM filmorate.films WHERE film_id = ?
            """;
    private static final String DELETE_FILM_GENRE_QUERY = """
            DELETE FROM filmorate.film_genres WHERE film_id = ?
            """;
    private static final String FIND_LIKE_BY_FILM_ID_QUERY = """
            SELECT COUNT(*) FROM FILMORATE.likes WHERE film_id = ? and user_id = ?
            """;
    private static final String FIND_POPULAR_FILM_QUERY = """
                SELECT DISTINCT f.*, m."name" AS mpa_name, COALESCE(likes.like_count, 0) AS like_count
                FROM filmorate.films f
                JOIN filmorate.MPA m ON f.MPA_ID = m.id
                LEFT JOIN (
                    SELECT l.FILM_ID, COUNT(l.FILM_ID) AS like_count
                    FROM filmorate.likes l
                    GROUP BY l.FILM_ID
                ) likes ON f.film_id = likes.FILM_ID
                LEFT JOIN filmorate.film_genres fg ON f.film_id = fg.film_id
                WHERE (? IS NULL OR fg.genre_id = ?)
                  AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
                ORDER BY like_count DESC
                LIMIT ?;
            """;
    private static final String FIND_LIKED_FILMS_BY_USER_ID_QUERY = """
            SELECT DISTINCT f.*, m."name" AS mpa_name, COALESCE(likes.like_count, 0) AS like_count
            FROM filmorate.films f
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            LEFT JOIN (
                SELECT l.FILM_ID, COUNT(*) AS like_count
                FROM filmorate.likes l
                GROUP BY l.FILM_ID
            ) likes ON f.film_id = likes.FILM_ID
            WHERE f.film_id IN (
                SELECT film_id FROM filmorate.films WHERE film_id IN (
                    SELECT l.FILM_ID
                    FROM filmorate.likes l
                    WHERE user_id = ?))
            ORDER BY like_count DESC
            """;
    private static final String FIND_FILMS_BY_DIRECTOR_AND_FILM_NAME_QUERY = """
            SELECT DISTINCT f.*, m."name" AS mpa_name, COALESCE(likes.like_count, 0) AS like_count
            FROM filmorate.films f
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            LEFT JOIN (
                 SELECT l.FILM_ID, COUNT(*) AS like_count
                 FROM filmorate.likes l
                 GROUP BY l.FILM_ID
            ) likes ON f.film_id = likes.FILM_ID
            WHERE f."name" LIKE ? OR f.FILM_ID IN (
                    SELECT FILM_ID
                    FROM filmorate.FILM_DIRECTORS fd2
                    WHERE DIRECTOR_ID IN (SELECT d2.DIRECTOR_ID
                                        FROM filmorate.DIRECTORS d2
                                        WHERE d2."name" LIKE ?))
            ORDER BY like_count DESC
            """;
    private static final String INSERT_FILM_LIKE_QUERY = """
            INSERT INTO filmorate.likes (film_id, user_id)
            VALUES(?, ?)
            """;
    private static final String DELETE_LIKE_BY_FILM_ID_QUERY = """
            DELETE FROM filmorate.likes WHERE film_id = ? and user_id = ?
            """;
    private static final String DELETE_ALL_LIKE_BY_FILM_ID_QUERY = """
            DELETE FROM filmorate.likes WHERE film_id = ?
            """;
    private static final String DELETE_ALL_LIKE_BY_USER_ID_QUERY = """
            DELETE FROM filmorate.likes WHERE user_id = ?
            """;
    private static final String INSERT_FILM_DIRECTOR_QUERY = """
            INSERT INTO filmorate.film_directors (director_id, film_id)
            VALUES(?, ?)
            """;
    private static final String DELETE_FILM_DIRECTOR_QUERY = """
            DELETE FROM filmorate.film_directors WHERE film_id = ?
            """;
    private static final String FIND_FILMS_BY_DIRECTOR_ID_ORDER_BY_RELEASE_DATE_QUERY = """
            SELECT f.*, m."name" AS mpa_name
            FROM filmorate.FILMS f
            JOIN filmorate.FILM_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            WHERE fd.DIRECTOR_ID = ?
            ORDER BY f.RELEASE_DATE
            """;
    private static final String FIND_FILMS_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY = """
            SELECT f.*, m."name" AS mpa_name, COALESCE(COUNT(l.FILM_ID), 0) AS count_likes
            FROM filmorate.FILMS f
            JOIN filmorate.FILM_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            LEFT JOIN filmorate.LIKES l ON f.FILM_ID = l.FILM_ID
            WHERE fd.DIRECTOR_ID = ?
            GROUP BY f.FILM_ID, m."name"
            ORDER BY COUNT_LIKES DESC
            """;
    private static final String RECOMMENDATIONS_QUERY = """
            SELECT DISTINCT f.*, m.ID AS mpa_id, m."name" AS mpa_name
              FROM FILMORATE.films AS f
              JOIN FILMORATE.likes AS l ON f.FILM_ID = l.film_id
              JOIN FILMORATE.MPA AS m ON f.MPA_ID = m.ID
              WHERE l.user_id = (
                  SELECT l2.user_id
                  FROM FILMORATE.likes AS l2
                  WHERE l2.film_id IN (
                      SELECT l1.film_id FROM FILMORATE.likes AS l1 WHERE l1.user_id = ?
                  )
                  AND l2.user_id != ?
                  GROUP BY l2.user_id
                  ORDER BY COUNT(l2.film_id) DESC
                  LIMIT 1
              )
              AND l.film_id NOT IN (
                  SELECT l3.film_id FROM FILMORATE.likes AS l3 WHERE l3.user_id = ?
              );
            """;


    public FilmDbStorageImpl(JdbcOperations jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc);
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public List<Film> searchByFilmsAndDirectors(String query, List<String> by) {
        String searchTitle = by.contains("title") ? "%" + query + "%" : null;
        String searchDirector = by.contains("director") ? "%" + query + "%" : null;
        return findMany(filmRowMapper, FIND_FILMS_BY_DIRECTOR_AND_FILM_NAME_QUERY, searchTitle, searchDirector);
    }

    @Override
    public List<Film> findLikedFilmsByUserId(long userId) {
        return jdbc.query(FIND_LIKED_FILMS_BY_USER_ID_QUERY, filmRowMapper, userId);
    }

    @Override
    public Optional<Film> findById(long id) {
        return findOne(filmRowMapper, FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(filmRowMapper, FIND_ALL_QUERY);
    }

    @Override
    public Film add(Film film) {
        long id = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                getMpaId(film)
        );
        film.setId(id);
        updateGenres(film);
        updateDirector(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                getMpaId(film),
                film.getId()
        );
        updateGenres(film);
        updateDirector(film);
        return film;
    }

    @Override
    public Collection<Film> findPopular(Integer count, Long genreId, Integer year) {
        Object[] params = {genreId, genreId, year, year, count};
        return findMany(filmRowMapper, FIND_POPULAR_FILM_QUERY, params);
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (!isLikeExists(filmId, userId)) {
            update(INSERT_FILM_LIKE_QUERY, filmId, userId);
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        delete(DELETE_LIKE_BY_FILM_ID_QUERY, filmId, userId);
    }

    @Override
    public void deleteAllLikeByFilmId(long filmId) {
        delete(DELETE_ALL_LIKE_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void deleteAllLikeByUserId(long id) {
        delete(DELETE_ALL_LIKE_BY_USER_ID_QUERY, id);
    }

    @Override
    public void delete(long id) {
        delete(DELETE_FILM_QUERY, id);
    }

    @Override
    public Collection<Film> findByDirectorId(int directorId, String sortBy) {
        if (sortBy.equals("likes"))
            return findMany(filmRowMapper, FIND_FILMS_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY, directorId);
        else
            return findMany(filmRowMapper, FIND_FILMS_BY_DIRECTOR_ID_ORDER_BY_RELEASE_DATE_QUERY, directorId);
    }

    @Override
    public Collection<Film> filmsRecommendations(long userId) {
        return findMany(filmRowMapper, RECOMMENDATIONS_QUERY, userId, userId, userId);
    }

    private Integer getMpaId(Film film) {
        return film.getMpa() != null ? film.getMpa().getId() : null;
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null) {
            delete(DELETE_FILM_GENRE_QUERY, film.getId());

            List<Object[]> batchArgs = film.getGenres().stream()
                    .distinct()
                    .map(genre -> new Object[]{genre.getId(), film.getId()})
                    .toList();

            jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, batchArgs);
        }
    }

    public boolean isLikeExists(long filmId, long userId) {
        List<Integer> countList = jdbc.query(FIND_LIKE_BY_FILM_ID_QUERY, (rs, rowNum) -> rs.getInt(1), filmId, userId);
        return !countList.isEmpty() && countList.get(0) > 0;
    }

    private void updateDirector(Film film) {
        delete(DELETE_FILM_DIRECTOR_QUERY, film.getId());

        if (film.getDirectors() != null) {
            List<Object[]> batchArgs = film.getDirectors().stream()
                    .distinct()
                    .map(director -> new Object[]{director.getId(), film.getId()})
                    .toList();

            jdbc.batchUpdate(INSERT_FILM_DIRECTOR_QUERY, batchArgs);
        }
    }
}
