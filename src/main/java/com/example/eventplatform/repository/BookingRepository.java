package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository
    extends JpaRepository<Booking, Long> {
}
