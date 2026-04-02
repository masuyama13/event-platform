package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class InvoiceService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");
    private static final String DEFAULT_CURRENCY = "cad";

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice createInvoiceIfNotExists(Booking booking) {

        Invoice existing = invoiceRepository.findByBookingId(booking.getId());

        if (existing != null) {
            return existing;
        }

        BigDecimal price = BigDecimal.valueOf(booking.getPrice())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = price.multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = price.add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalAmount(total);
        invoice.setCurrency(DEFAULT_CURRENCY);
        invoice.setStatus(InvoiceStatus.PENDING);

        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Invoice getInvoiceBySessionId(String stripeSessionId) {
        return invoiceRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Invoice saveCheckoutSession(Long invoiceId, String stripeSessionId) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setStripeSessionId(stripeSessionId);
        return invoiceRepository.save(invoice);
    }

    public Invoice markPaidBySessionId(String stripeSessionId, String stripePaymentIntentId) {
        Invoice invoice = getInvoiceBySessionId(stripeSessionId);
        return markPaid(invoice, stripeSessionId, stripePaymentIntentId);
    }

    public Invoice markPaidByInvoiceId(Long invoiceId, String stripeSessionId, String stripePaymentIntentId) {
        Invoice invoice = getInvoiceById(invoiceId);
        return markPaid(invoice, stripeSessionId, stripePaymentIntentId);
    }

    private Invoice markPaid(Invoice invoice, String stripeSessionId, String stripePaymentIntentId) {
        invoice.setStatus(InvoiceStatus.PAID);
        if (stripeSessionId != null && !stripeSessionId.isBlank()) {
            invoice.setStripeSessionId(stripeSessionId);
        }
        invoice.setStripePaymentIntentId(stripePaymentIntentId);
        invoice.setPaidAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }
}
