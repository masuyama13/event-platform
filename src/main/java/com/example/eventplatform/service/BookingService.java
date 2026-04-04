package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
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
    private PlanRepository planRepository;

    // Get all available plans
    public List<Plan> getAvailablePlans() {
        return planRepository.findAll();
    }

    // Get a specific plan detail
    public Plan getPlanDetail(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found: " + planId));
    }

    // Save confirmed booking to database with REQUESTED status
    public Booking confirmBooking(Long planId, LocalDate eventDate) {
        Plan selectedPlan = getPlanDetail(planId);

        // TODO: Replace with real profiles when authentication is ready
        CustomerProfile customerProfile = customerProfileRepository.findAll()
                .stream()
                .findFirst()
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
}
