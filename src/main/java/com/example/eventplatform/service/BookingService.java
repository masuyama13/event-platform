package com.example.eventplatform.service;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.BookingStatus;
import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // Get all available plans using real organizer data
    public List<Booking> getAvailablePlans() {
        List<Booking> plans = new ArrayList<>();

        // Fetch first organizer from database
        OrganizerProfile organizer = organizerProfileRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        String[] planNames = {"Plan A", "Plan B", "Plan C", "Plan D"};
        Double[] prices = {99.99, 149.99, 199.99, 249.99};
        LocalDate[] dates = {
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 2),
                LocalDate.of(2024, 12, 3),
                LocalDate.of(2024, 12, 4)
        };

        for (int i = 0; i < planNames.length; i++) {
            Booking plan = new Booking();
            plan.setPlanName(planNames[i]);
            plan.setPrice(prices[i]);
            plan.setEventDate(dates[i]);

            // Set real organizer from database
            if (organizer != null) {
                plan.setOrganizerProfile(organizer);
                plan.setPlannerName(organizer.getBusinessName());
            }

            plans.add(plan);
        }

        return plans;
    }

    // Get a specific plan detail for confirmation page
    public Booking getPlanDetail(String planName) {
        return getAvailablePlans().stream()
                .filter(plan -> plan.getPlanName().equals(planName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found: " + planName));
    }

    // Save confirmed booking to database with REQUESTED status
    public Booking confirmBooking(String planName, String organizerName,
                                  LocalDate eventDate, Double price) {

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
