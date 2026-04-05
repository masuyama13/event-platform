package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.service.InvoiceService;
import com.example.eventplatform.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingManagementController {

    private final BookingService bookingService;
    private final InvoiceService invoiceService;

    public BookingManagementController(BookingService bookingService, InvoiceService invoiceService) {
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/customer/bookings")
    public String customerBookings(Authentication authentication, Model model) {
        List<Booking> bookings = bookingService.getCustomerBookings(authentication.getName());
        model.addAttribute("bookings", bookings);
        model.addAttribute("invoicesByBookingId", invoiceService.findByBookingIds(extractBookingIds(bookings)));
        return "customer/bookings";
    }

    @GetMapping("/customer/bookings/{bookingId}")
    public String customerBookingDetail(@PathVariable Long bookingId,
                                        Authentication authentication,
                                        Model model) {
        Booking booking = bookingService.getCustomerBooking(bookingId, authentication.getName());
        model.addAttribute("booking", booking);
        model.addAttribute("invoice", invoiceService.findByBookingId(bookingId).orElse(null));
        return "customer/booking-detail";
    }

    @PostMapping("/customer/bookings/{bookingId}/cancel")
    public String cancelCustomerBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.cancelCustomerBooking(bookingId, authentication.getName());
        return "redirect:/customer/bookings";
    }

    @GetMapping("/organizer/bookings")
    public String organizerBookings(Authentication authentication, Model model) {
        List<Booking> bookings = bookingService.getOrganizerBookings(authentication.getName());
        model.addAttribute("bookings", bookings);
        model.addAttribute("invoicesByBookingId", invoiceService.findByBookingIds(extractBookingIds(bookings)));
        return "organizer/bookings";
    }

    @GetMapping("/organizer/bookings/{bookingId}")
    public String organizerBookingDetail(@PathVariable Long bookingId,
                                         Authentication authentication,
                                         Model model) {
        Booking booking = bookingService.getOrganizerBooking(bookingId, authentication.getName());
        model.addAttribute("booking", booking);
        model.addAttribute("invoice", invoiceService.findByBookingId(bookingId).orElse(null));
        return "organizer/booking-detail";
    }

    @PostMapping("/organizer/bookings/{bookingId}/approve")
    public String approveBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.approveBooking(bookingId, authentication.getName());
        return "redirect:/organizer/bookings";
    }

    @PostMapping("/organizer/bookings/{bookingId}/reject")
    public String rejectBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.rejectBooking(bookingId, authentication.getName());
        return "redirect:/organizer/bookings/" + bookingId;
    }

    private List<Long> extractBookingIds(List<Booking> bookings) {
        List<Long> bookingIds = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingIds.add(booking.getId());
        }
        return bookingIds;
    }
}
