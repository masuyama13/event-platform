package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BookingManagementController {

    private final BookingService bookingService;

    public BookingManagementController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/customer/bookings")
    public String customerBookings(Authentication authentication, Model model) {
        List<Booking> bookings = bookingService.getCustomerBookings(authentication.getName());
        model.addAttribute("bookings", bookings);
        return "customer-bookings";
    }

    @GetMapping("/customer/bookings/{bookingId}")
    public String customerBookingDetail(@PathVariable Long bookingId,
                                        Authentication authentication,
                                        Model model) {
        Booking booking = bookingService.getCustomerBooking(bookingId, authentication.getName());
        model.addAttribute("booking", booking);
        return "booking-detail";
    }

    @GetMapping("/organizer/bookings")
    public String organizerBookings(Authentication authentication, Model model) {
        List<Booking> bookings = bookingService.getOrganizerBookings(authentication.getName());
        model.addAttribute("bookings", bookings);
        return "organizer-bookings";
    }

    @PostMapping("/organizer/bookings/{bookingId}/approve")
    public String approveBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.approveBooking(bookingId, authentication.getName());
        return "redirect:/organizer/bookings";
    }

    @PostMapping("/organizer/bookings/{bookingId}/reject")
    public String rejectBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.rejectBooking(bookingId, authentication.getName());
        return "redirect:/organizer/bookings";
    }
}
