package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.Invoice;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BookingRepository bookingRepository;


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
