package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.Invoice;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String viewInvoice(@PathVariable Long bookingId, Model model) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Invoice invoice = invoiceService.createInvoiceIfNotExists(booking);

        model.addAttribute("invoice", invoice);
        model.addAttribute("booking", booking);

        return "invoice";
    }
}
