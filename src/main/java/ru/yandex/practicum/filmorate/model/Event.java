package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder(toBuilder = true)
public class Event {
    private Integer eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;
}
