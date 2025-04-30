package com.spring.ems.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.ems.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    Optional<Event> findByEventIdAndIsActiveTrue(Long eventId);
    List<Event> findByUserUserIdAndIsActiveTrue(Long organizerId);
    List<Event> findByCategoryAndIsActiveTrue(String category);
    List<Event> findByDateAfterAndIsActiveTrue(LocalDateTime date);
    List<Event> findByDateBeforeAndIsActiveTrue(LocalDateTime date);
    List<Event> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);
}
