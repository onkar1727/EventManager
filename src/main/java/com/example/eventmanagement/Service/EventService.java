package com.example.eventmanagement.Service;

import com.example.eventmanagement.Entity.Event;
import com.example.eventmanagement.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    private static final int MAX_PAGE_SIZE = 5;

    // Create Event
    public Event createEvent(Event event) {
        if (event.getDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        return eventRepository.save(event);
    }

    // Get Events with Pagination, Search, Filter
    public Page<Event> getEvents(
            String name,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        String n = (name == null || name.isBlank()) ? null : name.trim();
        String l = (location == null || location.isBlank()) ? null : location.trim();

        Set<String> allowed = Set.of("id", "name", "date", "location");
        String sortField = (sortBy == null || !allowed.contains(sortBy)) ? "date" : sortBy;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortField));

        return eventRepository.search(n, l, startDate, endDate, pageable);
    }

    // Get All Events
    public List<Event> getAllEvents() {
        return eventRepository.findAll(Sort.by("date").ascending());
    }

    // Get Event by ID
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    // Delete Event by ID
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Event with ID " + id + " does not exist");
        }
        eventRepository.deleteById(id);
    }

    // Get Event by Date Range
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByDateBetween(startDate, endDate); // Fixed field
    }

    // Download Event list for Export
    public List<Event> findAllForExport(String name, String location, LocalDate date) {
        return eventRepository.findAllForExport(
                (name == null || name.isBlank()) ? null : name.trim(),
                (location == null || location.isBlank()) ? null : location.trim(),
                date
        );
    }
}
