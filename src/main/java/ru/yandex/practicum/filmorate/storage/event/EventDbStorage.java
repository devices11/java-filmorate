package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventDbStorage {
    Collection<Event> findAllEventsByUserId(Long id);
    void addEvent(Integer userId, Event.EventType eventType, Event.Operation operation, Integer entityId);
}
