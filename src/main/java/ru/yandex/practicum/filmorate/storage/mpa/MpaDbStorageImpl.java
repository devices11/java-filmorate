package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorageImpl extends BaseStorage<Mpa> implements MpaDbStorage {
    private final MpaRowMapper mpaRowMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM filmorate.mpa ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM filmorate.mpa WHERE id = ?";

    public MpaDbStorageImpl(JdbcOperations jdbc, MpaRowMapper mpaRowMapper) {
        super(jdbc);
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Optional<Mpa> findById(long id) {
        return findOne(mpaRowMapper, FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Mpa> findAll() {
        return findMany(mpaRowMapper, FIND_ALL_QUERY);
    }
}
