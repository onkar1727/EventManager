package com.example.eventmanagement.Repository;

import com.example.eventmanagement.Entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {


    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Event> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    // Search by name and location (case-insensitive)
    Page<Event> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(String name, String location, Pageable pageable);

    List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Advanced filtering with optional parameters
    @Query(value = """
      SELECT e FROM Event e
      WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:startDate IS NULL OR e.date >= :startDate)
        AND (:endDate IS NULL OR e.date <= :endDate)
      """,
            countQuery = """
      SELECT COUNT(e) FROM Event e
      WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:startDate IS NULL OR e.date >= :startDate)
        AND (:endDate IS NULL OR e.date <= :endDate)
      """)
    Page<Event> search(
            @Param("name") String name,
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // Export events with optional filters
    @Query("""
      SELECT e FROM Event e
      WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:date IS NULL OR e.date = :date)
      ORDER BY e.date ASC
    """)
    List<Event> findAllForExport(
            @Param("name") String name,
            @Param("location") String location,
            @Param("date") LocalDate date
    );
}
