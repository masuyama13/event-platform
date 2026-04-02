package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    // Get all available plans from quotes table
    public List<Quote> getAvailablePlans() {
        return quoteRepository.findByStatus(QuoteStatus.PENDING);
    }

    // Get a specific plan detail from quotes table
    public Quote getPlanDetail(String planName) {
        return quoteRepository.findByStatus(QuoteStatus.PENDING)
                .stream()
                .filter(q -> q.getPlanName().equals(planName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found: " + planName));
    }

    // Save confirmed booking to database with REQUESTED status
    public Booking confirmBooking(String planName, String organizerName,
                                  LocalDate eventDate, BigDecimal price) {

        // TODO: Replace with real profiles when authentication is ready
        CustomerProfile customerProfile = customerProfileRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No customer profile found"));

        OrganizerProfile organizerProfile = organizerProfileRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No organizer profile found"));

        Booking booking = new Booking();
        booking.setPlanName(planName);
        booking.setPlannerName(organizerName);
        booking.setEventDate(eventDate);
        booking.setPrice(price);
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setCustomerProfile(customerProfile);
        booking.setOrganizerProfile(organizerProfile);

        return bookingRepository.save(booking);
    }
}