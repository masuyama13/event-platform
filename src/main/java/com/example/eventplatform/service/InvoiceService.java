package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice createInvoiceIfNotExists(Booking booking) {

        Invoice existing = invoiceRepository.findByBookingId(booking.getId());

        if (existing != null) {
            return existing;
        }

        double price = booking.getPrice();


        double tax = price * 0.10;
        tax = Math.round(tax * 100.0) / 100.0;


        double total = price + tax;
        total = Math.round(total * 100.0) / 100.0;

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalAmount(total);
        invoice.setStatus(InvoiceStatus.PENDING);

        return invoiceRepository.save(invoice);
    }
}
