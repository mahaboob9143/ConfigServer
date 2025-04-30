package com.spring.ems.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ems.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserIdAndIsActiveTrue(Long userId);
    List<Notification> findByEventEventIdAndIsActiveTrue(Long eventId);
    List<Notification> findByIsActiveTrue();
    List<Notification> findByUserUserId(Long userId);  
}
