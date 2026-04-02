package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // TODO: Replace with actual authenticated organizer
    private OrganizerProfile getTemporaryOrganizer() {
        return organizerProfileRepository.findAll().get(0);
    }

    // Create a new quote
    public Quote createQuote(String planName, BigDecimal price,
                             String description) {

        OrganizerProfile organizer = getTemporaryOrganizer();

        Quote quote = new Quote();
        quote.setOrganizer(organizer);
        quote.setPlanName(planName);
        quote.setPrice(price);
        quote.setDescription(description);
        quote.setStatus(QuoteStatus.PENDING);
        quote.setExpiresAt(LocalDateTime.now().plusDays(7));

        return quoteRepository.save(quote);
    }

    // Get all quotes by organizer
    public List<Quote> getQuotesByOrganizer(Long organizerId) {
        return quoteRepository.findByOrganizerId(organizerId);
    }

    // Update quote status
    public void updateQuoteStatus(Long quoteId, QuoteStatus status) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException(
                        "Quote not found: " + quoteId));

        quote.setStatus(status);
        quoteRepository.save(quote);
    }
}