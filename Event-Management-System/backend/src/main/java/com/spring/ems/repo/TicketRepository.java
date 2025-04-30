package com.spring.ems.repo;

import com.spring.ems.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	List<Ticket> findByUserUserIdAndIsActiveTrue(Long userId);

	List<Ticket> findByUserUserIdAndStatusAndIsActiveFalse(Long userId, String status);

	List<Ticket> findByStatusAndIsActiveTrue(String status);

	List<Ticket> findByStatusAndIsActiveFalse(String status);

	List<Ticket> findByEventEventIdAndIsActiveTrue(Long eventId);

	long countByEventEventIdAndIsActiveTrue(Long eventId);

	boolean existsByUserUserIdAndEventEventIdAndIsActiveTrue(Long userId, Long eventId);

	long countByUserUserId(Long userId);

	long countByUserUserIdAndIsActiveFalse(Long userId);
	List<Ticket> findByUserUserIdAndStatus(Long userId, String status);

	@Query("SELECT COUNT(DISTINCT t.event.eventId) FROM Ticket t WHERE t.user.userId = :userId")
	long countDistinctEventByUserUserId(@Param("userId") Long userId);
}
