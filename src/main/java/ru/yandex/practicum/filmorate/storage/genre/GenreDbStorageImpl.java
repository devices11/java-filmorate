package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorageImpl extends BaseStorage<Genre> implements GenreDbStorage {
    private final GenreRowMapper genreRowMapper;

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM filmorate.genre ORDER BY genre_id";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
             SELECT fg.genre_id, g."name" FROM filmorate.film_genre fg
             JOIN filmorate.genre g ON fg.genre_id = g.genre_id
             WHERE film_id = ?
            """;
    private static final String FIND_BY_ID_GENRES_QUERY = "SELECT * FROM filmorate.genre WHERE genre_id = ?";

    public GenreDbStorageImpl(JdbcTemplate jdbc, GenreRowMapper genreRowMapper) {
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
}
