package com.example.eventmanagement.Controller;

import com.example.eventmanagement.Entity.Event;
import com.example.eventmanagement.Service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173") // allow Vue frontend
@RestController
@RequestMapping("/events") // <-- Changed to match frontend calls
public class EventController {

    @Autowired
    private EventService eventService;


    // Create Event
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }


    // Paginated + Filtered Events
    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location
    ) {
        Page<Event> events = eventService.getEvents(name, location, page, size);
        return ResponseEntity.ok(events);
    }

    // Get All Unique Locations
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations() {
        List<String> locations = eventService.getAllEvents().stream()
                .map(Event::getLocation)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }


    // Delete Event by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event with ID " + id + " deleted successfully");
    }
}
