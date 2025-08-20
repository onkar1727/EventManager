package com.example.eventmanagement.Controller;

import com.example.eventmanagement.Entity.Event;
import com.example.eventmanagement.Service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/events") //
public class EventController
{

    @Autowired
    private EventService eventService;


    // Create Event
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event)
    {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }


    // Paginated + Filtered Events
    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        Page<Event> events = eventService.getEvents(name, location, startDate, endDate, page, size, sortBy, sortDir);
        return ResponseEntity.ok(events);
    }

    // Get All Unique Locations
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations()
    {
        List<String> locations = eventService.getAllEvents().stream()
                .map(Event::getLocation)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }


    // Delete Event by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id)
    {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event with ID " + id + " deleted successfully");
    }


    //To Download the Event list in the Excel format
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Events");

            var events = eventService.findAllForExport(name, location, date);

            // header
            String[] cols = {"ID", "Name", "Date", "Location", "Description"};
            var header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            // data
            int r = 1;
            for (var e : events) {
                var row = sheet.createRow(r++);
                row.createCell(0).setCellValue(e.getId() == null ? "" : e.getId().toString());
                row.createCell(1).setCellValue(e.getName());
                row.createCell(2).setCellValue(e.getDate() == null ? "" : e.getDate().toString());
                row.createCell(3).setCellValue(e.getLocation());
                row.createCell(4).setCellValue(e.getDescription());
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            var out = new java.io.ByteArrayOutputStream();
            wb.write(out);
            byte[] bytes = out.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Event_List.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to export events", ex);
        }
    }

    public List<Event> filterEventsByDateRange( @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        return eventService.getEventsByDateRange(startDate, endDate);
    }
}
