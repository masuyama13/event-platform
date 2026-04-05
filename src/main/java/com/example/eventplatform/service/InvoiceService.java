package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InvoiceService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");
    private static final String DEFAULT_CURRENCY = "cad";

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, BookingRepository bookingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
    }

    public Invoice createInvoiceIfNotExists(Booking booking) {

        Invoice existing = invoiceRepository.findByBookingId(booking.getId());

        if (existing != null) {
            return existing;
        }

        BigDecimal price = booking.getPrice()
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

    public Optional<Invoice> findByBookingId(Long bookingId) {
        return Optional.ofNullable(invoiceRepository.findByBookingId(bookingId));
    }

    public Map<Long, Invoice> findByBookingIds(Collection<Long> bookingIds) {
        Map<Long, Invoice> invoicesByBookingId = new HashMap<>();
        if (bookingIds == null || bookingIds.isEmpty()) {
            return invoicesByBookingId;
        }

        for (Invoice invoice : invoiceRepository.findByBookingIdIn(bookingIds)) {
            invoicesByBookingId.put(invoice.getBooking().getId(), invoice);
        }
        return invoicesByBookingId;
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
        Booking booking = invoice.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        return invoiceRepository.save(invoice);
    }
}
