package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booking = new Booking();
        booking.setId(1L);
        booking.setPrice(new BigDecimal("100.00"));
    }

    @Test
    void createInvoice_ShouldCalculateTotalCorrectly() {

        when(invoiceRepository.findByBookingId(1L)).thenReturn(null);
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Invoice invoice = invoiceService.createInvoiceIfNotExists(booking);

        assertNotNull(invoice);
        assertEquals(new BigDecimal("105.00"), invoice.getTotalAmount());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
    }

    @Test
    void createInvoice_ShouldReturnExistingInvoice() {

        Invoice existing = new Invoice();
        existing.setId(10L);

        when(invoiceRepository.findByBookingId(1L)).thenReturn(existing);

        Invoice result = invoiceService.createInvoiceIfNotExists(booking);

        assertEquals(existing, result);
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void markPaidByInvoiceId_ShouldCompleteBooking() {
        Invoice invoice = new Invoice();
        invoice.setId(10L);
        invoice.setBooking(booking);

        when(invoiceRepository.findById(10L)).thenReturn(java.util.Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Invoice result = invoiceService.markPaidByInvoiceId(10L, "sess_123", "pi_123");

        assertEquals(InvoiceStatus.PAID, result.getStatus());
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }
}
