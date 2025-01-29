package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Repository
public class GenreDbStorageImpl extends BaseStorage<Genre> implements GenreDbStorage {
    private final GenreRowMapper genreRowMapper;

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM filmorate.genres ORDER BY genre_id";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
             SELECT fg.genre_id, g."name" FROM filmorate.film_genres fg
             JOIN filmorate.genres g ON fg.genre_id = g.genre_id
             WHERE film_id = ?
            """;
    private static final String FIND_BY_ID_GENRES_QUERY = "SELECT * FROM filmorate.genres WHERE genre_id = ?";
    private static final String FIND_ALL_BY_FILMS = """
                SELECT fg.film_id, g.*
                FROM filmorate.film_genres fg
                JOIN filmorate.genres g ON fg.genre_id = g.genre_id
            """;
    private static final String DELETE_CONNECTION_BY_FILM_ID_QUERY = """
             DELETE FROM filmorate.film_genres
             WHERE film_id = ?
            """;

    public GenreDbStorageImpl(JdbcOperations jdbc, GenreRowMapper genreRowMapper) {
        super(jdbc);
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(genreRowMapper, FIND_BY_ID_GENRES_QUERY, id);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(genreRowMapper, FIND_ALL_GENRES_QUERY);
    }

    @Override
    public Collection<Genre> findAllByFilmId(long id) {
        return findMany(genreRowMapper, FIND_ALL_BY_FILM_ID_QUERY, id);
    }

    public Map<Long, List<Genre>> findAllByFilms() {
        List<Map<String, Object>> rows = jdbc.queryForList(FIND_ALL_BY_FILMS);

        return rows.stream().collect(groupingBy(
                        row -> ((Number) row.get("FILM_ID")).longValue(),
                        mapping(row -> Genre.builder()
                                .id((Integer) row.get("GENRE_ID"))
                                .name((String) row.get("name"))
                                .build(), Collectors.toList())
                )
        );
    }

    @Override
    public void deleteConnectionByFilmId(long id) {
        delete(DELETE_CONNECTION_BY_FILM_ID_QUERY, id);
    }
}
