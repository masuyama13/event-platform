package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Quote;
import com.example.eventplatform.entity.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // Get all quotes by organizer
    List<Quote> findByOrganizerId(Long organizerId);

    // Get all active/pending quotes
    List<Quote> findByStatus(QuoteStatus status);

    // Get quote by organizer and plan name
    List<Quote> findByOrganizerIdAndPlanName(Long organizerId, String planName);
}