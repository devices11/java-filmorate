package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Event {
    private Integer eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }
}
