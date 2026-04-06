package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.BookingStatus;
import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.Invoice;
import com.example.eventplatform.entity.InvoiceStatus;
import com.example.eventplatform.entity.User;
import com.stripe.model.Event;
import com.example.eventplatform.service.InvoiceService;
import com.example.eventplatform.service.PaymentService;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private Authentication authentication;

    @Test
    void createCheckoutSession_shouldRedirectToStripeWhenInvoiceIsPayable() throws Exception {
        Invoice invoice = invoice(10L, 21L, BookingStatus.APPROVED, InvoiceStatus.PENDING, "customer@test.com");
        Session session = mock(Session.class);

        when(authentication.getName()).thenReturn("customer@test.com");
        when(invoiceService.getInvoiceById(10L)).thenReturn(invoice);
        when(paymentService.createCheckoutSession(invoice, "customer@test.com")).thenReturn(session);
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getUrl()).thenReturn("https://checkout.stripe.com/pay/cs_test_123");

        PaymentController controller = new PaymentController(invoiceService, paymentService);

        String view = controller.createCheckoutSession(10L, authentication);

        assertThat(view).isEqualTo("redirect:https://checkout.stripe.com/pay/cs_test_123");
        verify(invoiceService).saveCheckoutSession(10L, "cs_test_123");
    }

    @Test
    void createCheckoutSession_shouldRedirectBackToInvoiceWhenAlreadyPaid() throws Exception {
        Invoice invoice = invoice(10L, 21L, BookingStatus.APPROVED, InvoiceStatus.PAID, "customer@test.com");
        when(authentication.getName()).thenReturn("customer@test.com");
        when(invoiceService.getInvoiceById(10L)).thenReturn(invoice);

        PaymentController controller = new PaymentController(invoiceService, paymentService);

        String view = controller.createCheckoutSession(10L, authentication);

        assertThat(view).isEqualTo("redirect:/invoices/view/21");
    }

    @Test
    void paymentSuccess_shouldAttachInvoiceWhenSessionIdProvided() {
        Invoice invoice = invoice(10L, 21L, BookingStatus.COMPLETED, InvoiceStatus.PAID, "customer@test.com");
        Model model = mock(Model.class);

        when(authentication.getName()).thenReturn("customer@test.com");
        when(invoiceService.getInvoiceBySessionId("cs_test_123")).thenReturn(invoice);

        PaymentController controller = new PaymentController(invoiceService, paymentService);

        String view = controller.paymentSuccess("cs_test_123", authentication, model);

        assertThat(view).isEqualTo("shared/payment-success");
        verify(model).addAttribute("invoice", invoice);
    }

    @Test
    void handleStripeWebhook_shouldMarkInvoicePaidBySessionId() {
        Event event = mock(Event.class);
        Session session = mock(Session.class);

        when(paymentService.parseWebhookEvent("payload", "signature")).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(paymentService.extractCheckoutSession(event)).thenReturn(session);
        when(session.getMetadata()).thenReturn(java.util.Map.of("invoiceId", "10"));
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getPaymentIntent()).thenReturn("pi_123");

        PaymentController controller = new PaymentController(invoiceService, paymentService);

        assertThat(controller.handleStripeWebhook("payload", "signature").getBody()).isEqualTo("received");
        verify(invoiceService).markPaidBySessionId("cs_test_123", "pi_123");
        verify(invoiceService, never()).markPaidByInvoiceId(10L, "cs_test_123", "pi_123");
    }

    @Test
    void handleStripeWebhook_shouldFallbackToInvoiceIdWhenSessionLookupFails() {
        Event event = mock(Event.class);
        Session session = mock(Session.class);

        when(paymentService.parseWebhookEvent("payload", "signature")).thenReturn(event);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(paymentService.extractCheckoutSession(event)).thenReturn(session);
        when(session.getMetadata()).thenReturn(java.util.Map.of("invoiceId", "10"));
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getPaymentIntent()).thenReturn("pi_123");
        doThrow(new RuntimeException("session not found"))
                .when(invoiceService).markPaidBySessionId("cs_test_123", "pi_123");

        PaymentController controller = new PaymentController(invoiceService, paymentService);

        assertThat(controller.handleStripeWebhook("payload", "signature").getBody()).isEqualTo("received");
        verify(invoiceService).markPaidByInvoiceId(10L, "cs_test_123", "pi_123");
    }

    private Invoice invoice(Long invoiceId,
                            Long bookingId,
                            BookingStatus bookingStatus,
                            InvoiceStatus invoiceStatus,
                            String ownerEmail) {
        User user = new User();
        user.setEmail(ownerEmail);

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setUser(user);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(bookingStatus);
        booking.setCustomerProfile(customerProfile);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus(invoiceStatus);
        invoice.setBooking(booking);
        return invoice;
    }
}
