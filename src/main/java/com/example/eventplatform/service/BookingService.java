package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private PlanRepository planRepository;

    // Get available plans for a specific organizer
    public List<Plan> getAvailablePlans(Long organizerId) {
        return planRepository.findByOrganizerIdOrderByUpdatedAtDesc(organizerId);
    }

    // Get a specific plan detail
    public Plan getPlanDetail(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found: " + planId));
    }

    // Save confirmed booking to database with REQUESTED status
    public Booking confirmBooking(Long planId, LocalDate eventDate, String customerEmail) {
        LocalDate earliestBookingDate = LocalDate.now().plusWeeks(1);
        if (eventDate.isBefore(earliestBookingDate)) {
            throw new RuntimeException("Event date must be at least one week from today");
        }

        if (bookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                customerEmail, planId, eventDate)) {
            throw new RuntimeException("You already have a booking request for this plan and date");
        }

        Plan selectedPlan = getPlanDetail(planId);

        CustomerProfile customerProfile = customerProfileRepository.findByUserEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException(
                        "No customer profile found"));

        OrganizerProfile organizerProfile = selectedPlan.getOrganizer();
        if (organizerProfile == null) {
            throw new RuntimeException("No organizer profile found for plan: " + planId);
        }

        Booking booking = new Booking();
        booking.setPlan(selectedPlan);
        booking.setPlannerName(organizerProfile.getBusinessName());
        booking.setEventDate(eventDate);
        booking.setPrice(selectedPlan.getPrice());
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setCustomerProfile(customerProfile);
        booking.setOrganizerProfile(organizerProfile);

        return bookingRepository.save(booking);
    }

    public List<Booking> getCustomerBookings(String customerEmail) {
        return bookingRepository.findByCustomerProfileUserEmailOrderByCreatedAtDesc(customerEmail);
    }

    public List<Booking> getOrganizerBookings(String organizerEmail) {
        return bookingRepository.findByOrganizerProfileUserEmailOrderByCreatedAtDesc(organizerEmail);
    }

    public Booking approveBooking(Long bookingId, String organizerEmail) {
        Booking booking = getOwnedOrganizerBooking(bookingId, organizerEmail);
        booking.setStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }

    public Booking rejectBooking(Long bookingId, String organizerEmail) {
        Booking booking = getOwnedOrganizerBooking(bookingId, organizerEmail);
        booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    public Booking getCustomerBooking(Long bookingId, String customerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (!booking.getCustomerProfile().getUser().getEmail().equals(customerEmail)) {
            throw new RuntimeException("Booking does not belong to customer");
        }

        return booking;
    }

    public Booking getOrganizerBooking(Long bookingId, String organizerEmail) {
        return getOwnedOrganizerBooking(bookingId, organizerEmail);
    }

    private Booking getOwnedOrganizerBooking(Long bookingId, String organizerEmail) {
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("No organizer profile found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (!booking.getOrganizerProfile().getId().equals(organizerProfile.getId())) {
            throw new RuntimeException("Booking does not belong to organizer");
        }

        return booking;
    }
}
