package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.Collection;

public interface EventDbStorage {
    Collection<Event> findAllEventsByUserId(Long id);

    void addEvent(Integer userId, EventType eventType, Operation operation, Integer entityId);
}
