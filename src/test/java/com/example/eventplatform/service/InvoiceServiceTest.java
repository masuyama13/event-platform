package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
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
}