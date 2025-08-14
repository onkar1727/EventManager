package com.example.eventmanagement.Service;

import com.example.eventmanagement.Entity.Event;
import com.example.eventmanagement.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    private static final int MAX_PAGE_SIZE = 5;

    // ----------------------------
    // Create Event
    // ----------------------------
    public Event createEvent(Event event) {
        // Validate date is in the future
        if (event.getDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        return eventRepository.save(event);
    }

    // ----------------------------
    // Get Events with Pagination, Search, Filter
    // ----------------------------
    public Page<Event> getEvents(String name, String location, int page, int size) {
        int pageSize = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("date").ascending());

        // If both filters are empty
        if ((name == null || name.isBlank()) && (location == null || location.isBlank())) {
            return eventRepository.findAll(pageable);
        }

        // If only name is given
        if (location == null || location.isBlank()) {
            return eventRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        // If only location is given
        if (name == null || name.isBlank()) {
            return eventRepository.findByLocationContainingIgnoreCase(location, pageable);
        }

        // If both are given
        return eventRepository.findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(name, location, pageable);
    }

    // ----------------------------
    // Get All Events (for Locations API)
    // ----------------------------
    public List<Event> getAllEvents() {
        return eventRepository.findAll(Sort.by("date").ascending());
    }

    // ----------------------------
    // Get Event by ID
    // ----------------------------
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    // ----------------------------
    // Delete Event by ID
    // ----------------------------
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Event with ID " + id + " does not exist");
        }
        eventRepository.deleteById(id);
    }
}
