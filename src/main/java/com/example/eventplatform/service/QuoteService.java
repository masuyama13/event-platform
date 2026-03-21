package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.QuoteRepository;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
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
    private BookingRepository bookingRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // TODO: Replace with actual authenticated organizer when authentication is implemented
    private OrganizerProfile getTemporaryOrganizer() {
        return organizerProfileRepository.findAll().get(0);
    }

    // Create a new quote for a specific booking
    public Quote createQuote(Long bookingId, BigDecimal amount, String description) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        OrganizerProfile organizer = getTemporaryOrganizer();

        Quote quote = new Quote();
        quote.setBooking(booking);
        quote.setOrganizerProfile(organizer);
        quote.setQuotedAmount(amount);
        quote.setDescription(description);
        quote.setStatus(QuoteStatus.PENDING); // Initial status
        quote.setExpiresAt(LocalDateTime.now().plusDays(7)); // Set expiration date

        return quoteRepository.save(quote);
    }

    // Retrieve all quotes related to a specific booking
    public List<Quote> getQuotesByBooking(Long bookingId) {
        return quoteRepository.findByBookingId(bookingId);
    }

    // Update the status of a quote (e.g., ACCEPTED or REJECTED)
    public void updateQuoteStatus(Long quoteId, QuoteStatus status) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        // 1. Update the selected quote's status
        quote.setStatus(status);
        quoteRepository.save(quote);

        // 2. Only run additional logic if the quote is ACCEPTED
        if (status == QuoteStatus.ACCEPTED) {

            Long bookingId = quote.getBooking().getId();

            // 2-1. Retrieve all quotes related to the same booking
            List<Quote> quotes = quoteRepository.findByBookingId(bookingId);

            for (Quote q : quotes) {
                // Reject all other quotes except the selected one
                if (!q.getId().equals(quoteId)) {
                    q.setStatus(QuoteStatus.REJECTED);
                    quoteRepository.save(q);
                }
            }

            // 3. Update the booking status to CONFIRMED
            Booking booking = quote.getBooking();
            booking.setStatus(BookingStatus.ACCEPTED);
        }
    }
}