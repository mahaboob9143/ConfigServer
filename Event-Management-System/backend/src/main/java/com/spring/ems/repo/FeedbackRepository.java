package com.spring.ems.repo;

import com.spring.ems.entity.Feedback;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByEventEventIdAndIsActiveTrue(Long eventId);
    List<Feedback> findByUserUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event.eventId = :eventId AND f.isActive = true")
    Optional<Double> findAverageRatingByEventId(@Param("eventId") Long eventId);

    List<Feedback> findByEventEventIdAndIsActiveTrueOrderByFeedbackDateDesc(Long eventId);
    List<Feedback> findByEventEventIdAndIsActiveTrueOrderByRatingDesc(Long eventId);
    List<Feedback> findByEventEventIdAndIsActiveTrueOrderByRatingAsc(Long eventId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.event.eventId = :eventId AND f.isActive = true")
    long countByEventId(@Param("eventId") Long eventId);
    
    long countByUserUserId(Long userId);
    
    boolean existsByUserUserIdAndEventEventIdAndIsActiveTrue(Long userId, Long eventId);
}
