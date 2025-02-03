package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .timestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli())
                .userId(rs.getLong("user_id"))
                .eventType(Event.EventType.valueOf(rs.getString("event_type")))
                .operation(Event.Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
