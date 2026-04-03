package com.example.eventplatform.controller;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InvoiceControllerTest {

    @Test
    void viewInvoice_ShouldReturnInvoicePage() {

        InvoiceService invoiceService = mock(InvoiceService.class);
        BookingRepository bookingRepository = mock(BookingRepository.class);
        Model model = mock(Model.class);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPrice(new BigDecimal("100"));

        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(invoiceService.createInvoiceIfNotExists(booking)).thenReturn(invoice);

        InvoiceController controller =
                new InvoiceController(invoiceService, bookingRepository);

        String view = controller.viewInvoice(1L, model);

        assertEquals("invoice", view);
        verify(model).addAttribute("invoice", invoice);
        verify(model).addAttribute("booking", booking);
    }
}
