package com.example.eventmanagement.Repository;

import com.example.eventmanagement.Entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>
{
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Event> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    Page<Event> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(String name, String location, Pageable pageable);
}
