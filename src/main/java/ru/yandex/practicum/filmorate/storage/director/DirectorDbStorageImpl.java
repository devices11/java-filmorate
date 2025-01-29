package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Repository
public class DirectorDbStorageImpl extends BaseStorage<Director> implements DirectorDbStorage {
    private final DirectorRowMapper directorRowMapper;

    private static final String FIND_ALL_QUERY = """
            SELECT * FROM filmorate.directors
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM filmorate.directors
            WHERE DIRECTOR_ID = ?
            """;
    private static final String INSERT_DIRECTOR_QUERY = """
            INSERT INTO filmorate.directors ("name")
            VALUES(?);
            """;
    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE filmorate.directors
            SET "name" = ?
            WHERE director_id = ?
            """;
    private static final String DELETE_BY_ID_QUERY = """
            DELETE FROM filmorate.directors
            WHERE DIRECTOR_ID = ?
            """;
    private static final String FIND_ALL_BY_FILMS = """
                SELECT fd.film_id, d.*
                FROM filmorate.film_directors fd
                JOIN filmorate.directors d ON fd.director_id = d.director_id
            """;
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
             SELECT fd.director_id, d."name" FROM filmorate.film_directors fd
             JOIN filmorate.directors d ON fd.director_id = d.director_id
             WHERE film_id = ?
            """;

    public DirectorDbStorageImpl(JdbcOperations jdbc, DirectorRowMapper directorRowMapper) {
        super(jdbc);
        this.directorRowMapper = directorRowMapper;
    }


    @Override
    public Optional<Director> findById(long id) {
        return findOne(directorRowMapper, FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Director> findAll() {
        return findMany(directorRowMapper, FIND_ALL_QUERY);
    }

    @Override
    public Director add(Director director) {
        int id = insert(INSERT_DIRECTOR_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_DIRECTOR_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public void delete(long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Director> findAllByFilmId(long id) {
        return findMany(directorRowMapper, FIND_ALL_BY_FILM_ID_QUERY, id);
    }

    public Map<Long, List<Director>> findAllByFilms() {
        List<Map<String, Object>> rows = jdbc.queryForList(FIND_ALL_BY_FILMS);

        return rows.stream().collect(groupingBy(
                        row -> ((Number) row.get("FILM_ID")).longValue(),
                        mapping(row -> Director.builder()
                                .id((Integer) row.get("director_id"))
                                .name((String) row.get("name"))
                                .build(), Collectors.toList())
                )
        );
    }
}
