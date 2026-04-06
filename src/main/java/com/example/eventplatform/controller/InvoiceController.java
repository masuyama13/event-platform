package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.BookingStatus;
import com.example.eventplatform.entity.Invoice;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final BookingRepository bookingRepository;

    public InvoiceController(InvoiceService invoiceService, BookingRepository bookingRepository) {
        this.invoiceService = invoiceService;
        this.bookingRepository = bookingRepository;
    }


    @GetMapping("/view/{bookingId}")
    public String viewInvoice(@PathVariable Long bookingId, Authentication authentication, Model model) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found: " + bookingId));

        ensureBookingOwner(booking, authentication);
        if (booking.getStatus() != BookingStatus.APPROVED
                && booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Payment is available only after organizer approval");
        }

        Invoice invoice = invoiceService.createInvoiceIfNotExists(booking);

        model.addAttribute("invoice", invoice);
        model.addAttribute("booking", booking);

        return "customer/invoice";
    }

    private void ensureBookingOwner(Booking booking, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication is required");
        }

        String ownerEmail = booking.getCustomerProfile().getUser().getEmail();
        if (!ownerEmail.equals(authentication.getName())) {
            throw new AccessDeniedException("You do not have access to this booking");
        }
    }
}
