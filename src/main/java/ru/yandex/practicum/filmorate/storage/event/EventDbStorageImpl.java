package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.exception.InternalServerException;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.List;

@Repository
public class EventDbStorageImpl extends BaseStorage<Event> implements EventDbStorage {
    private final EventRowMapper eventRowMapper;
    private final UserDbStorage userDbStorage;

    private static final String FIND_ALL_BY_USER_ID_QUERY = "SELECT * FROM filmorate.events WHERE user_id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO filmorate.events(
            user_id,
            event_type,
            operation,
            entity_id)
            VALUES (?, ?, ?, ?)""";


    public EventDbStorageImpl(JdbcOperations jdbc, EventRowMapper eventRowMapper, UserDbStorage userDbStorage) {
        super(jdbc);
        this.eventRowMapper = eventRowMapper;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public void addEvent(Integer userId, EventType eventType, Operation operation, Integer entityId) {
        int updatedRows = jdbc.update(INSERT_QUERY,
                userId,
                eventType.toString(),
                operation.toString(),
                entityId);
        if (updatedRows == 0) throw new InternalServerException("Произошла ошибка создания события");
    }

    @Override
    public List<Event> findAllEventsByUserId(Long id) {
        userDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден, id=" + id));
        return jdbc.query(FIND_ALL_BY_USER_ID_QUERY, eventRowMapper, id);
    }
}
