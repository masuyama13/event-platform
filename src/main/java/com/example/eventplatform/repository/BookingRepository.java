package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository
    extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerProfileUserEmailOrderByCreatedAtDesc(String email);

    List<Booking> findByOrganizerProfileUserEmailOrderByCreatedAtDesc(String email);

    boolean existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
            String email,
            Long planId,
            LocalDate eventDate
    );
}
