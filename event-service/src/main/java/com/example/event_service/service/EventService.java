package com.example.event_service.service;


import com.example.event_service.model.Event;
import com.example.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<Event> getAllEvents() {
        return repo.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return repo.findById(id);
    }

    public Event createEvent(Event event) {
        event.setAvailableSeats(event.getTotalSeats());
        return repo.save(event);
    }

    public Optional<Event> reserveSeat(Long eventId) {
        Optional<Event> opt = repo.findById(eventId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Event event = opt.get();
        if (event.getAvailableSeats() <= 0) {
            return Optional.empty();
        }
        event.setAvailableSeats(event.getAvailableSeats() - 1);
        repo.save(event);
        return Optional.of(event);
    }

    public Optional<Event> releaseSeat(Long eventId) {
        Optional<Event> opt = repo.findById(eventId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Event event = opt.get();
        event.setAvailableSeats(event.getAvailableSeats() + 1);
        repo.save(event);
        return Optional.of(event);
    }
}