package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
            SELECT f.*, m."name" AS mpa_name
            FROM filmorate.films f
            JOIN filmorate.MPA m ON f.MPA_ID = m.id
            LEFT JOIN (
                SELECT l.FILM_ID, COUNT(l.FILM_ID) AS like_count
                FROM filmorate.likes l
                GROUP BY l.FILM_ID
            ) likes ON f.film_id = likes.FILM_ID
            ORDER BY like_count DESC
            LIMIT ?;
            """;
    private static final String INSERT_FILM_LIKE_QUERY = """
            INSERT INTO filmorate.likes (film_id, user_id)
            VALUES(?, ?)
            """;
    private static final String DELETE_LIKE_BY_FILM_ID_QUERY = """
            DELETE FROM filmorate.likes WHERE film_id = ? and user_id = ?
            """;

    public FilmDbStorageImpl(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc);
        this.filmRowMapper = filmRowMapper;
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
        return film;
    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        return findMany(filmRowMapper, FIND_POPULAR_FILM_QUERY, count);
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
    public void delete(long id) {
        delete(DELETE_FILM_QUERY, id);
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
        Integer count = jdbc.queryForObject(FIND_LIKE_BY_FILM_ID_QUERY, Integer.class, filmId, userId);
        return count != null && count > 0;
    }
}
