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

    // TODO: Replace with real CustomerProfileRepository when authentication is ready
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    // TODO: Replace with real OrganizerProfileRepository when authentication is ready
    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

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


        // TODO: Replace with real profiles when authentication is ready
        // Fetch first available profiles from database as temporary solution
        CustomerProfile customerProfile = customerProfileRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No customer profile found"));

        OrganizerProfile organizerProfile = organizerProfileRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No organizer profile found"));

        Booking booking = new Booking();

        // Set only required fields for now
        booking.setPlanName(planName);
        booking.setPlannerName(plannerName);
        booking.setEventDate(eventDate);
        booking.setPrice(price);
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setCustomerProfile(customerProfile);
        booking.setOrganizerProfile(organizerProfile);
        // Set status as REQUESTED
        booking.setStatus(BookingStatus.REQUESTED);


        return bookingRepository.save(booking);
    }
}
