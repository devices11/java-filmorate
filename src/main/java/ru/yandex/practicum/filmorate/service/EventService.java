package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventDbStorage storage;

    public void addEvent(Integer userId, Event.EventType eventType, Event.Operation operation, Integer entityId){
        storage.addEvent(userId, eventType, operation, entityId);
    }

    public List<Event> findAllEventsByUserId(Long id){
        return (List<Event>) storage.findAllEventsByUserId(id);
    }
}
