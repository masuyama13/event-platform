package com.example.eventplatform.service;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.BookingStatus;
import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // TODO: Replace with real CustomerProfile from authentication later
    private CustomerProfile getTemporaryCustomerProfile() {
        CustomerProfile customer = new CustomerProfile();
        customer.setId(1L);
        customer.setName("Temporary Customer");
        return customer;
    }

    // TODO: Replace with real OrganizerProfile from authentication later
    private OrganizerProfile getTemporaryOrganizerProfile() {
        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(1L);
        organizer.setName("Temporary Organizer");
        return organizer;
    }

    // Get all available plans
    // TODO: Replace with real data from database later
    public List<Booking> getAvailablePlans() {
        List<Booking> plans = new ArrayList<>();

        Booking planA = new Booking();
        planA.setPlannerName("John Planner");
        planA.setPlanName("Plan A");
        planA.setPrice(99.99);
        planA.setEventDate(LocalDate.of(2024, 12, 1));

        Booking planB = new Booking();
        planB.setPlannerName("John Planner");
        planB.setPlanName("Plan B");
        planB.setPrice(149.99);
        planB.setEventDate(LocalDate.of(2024, 12, 2));

        Booking planC = new Booking();
        planC.setPlannerName("John Planner");
        planC.setPlanName("Plan C");
        planC.setPrice(199.99);
        planC.setEventDate(LocalDate.of(2024, 12, 3));

        Booking planD = new Booking();
        planD.setPlannerName("John Planner");
        planD.setPlanName("Plan D");
        planD.setPrice(249.99);
        planD.setEventDate(LocalDate.of(2024, 12, 4));

        plans.add(planA);
        plans.add(planB);
        plans.add(planC);
        plans.add(planD);

        return plans;
    }

    // Get a specific plan detail for confirmation page
    public Booking getPlanDetail(String planName) {
        return getAvailablePlans().stream()
                .filter(plan -> plan.getPlanName().equals(planName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planName));
    }

    // Save confirmed booking to database with REQUESTED status
    public Booking confirmBooking(String planName, String plannerName,
                                  LocalDate eventDate, Double price) {

        Booking booking = new Booking();

        // Set only required fields for now
        booking.setPlanName(planName);
        booking.setPlannerName(plannerName);
        booking.setEventDate(eventDate);
        booking.setPrice(price);

        // Set status as REQUESTED
        booking.setStatus(BookingStatus.REQUESTED);

        // TODO: Replace with real profiles when authentication is ready
        booking.setCustomerProfile(getTemporaryCustomerProfile());
        booking.setOrganizerProfile(getTemporaryOrganizerProfile());

        return bookingRepository.save(booking);
    }
}
