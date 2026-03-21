package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByBookingId(Long bookingId);

}