package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Invoice;
import com.example.eventplatform.entity.InvoiceStatus;
import com.example.eventplatform.service.InvoiceService;
import com.example.eventplatform.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    public PaymentController(InvoiceService invoiceService, PaymentService paymentService) {
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/checkout/{invoiceId}")
    public String createCheckoutSession(@PathVariable Long invoiceId, Authentication authentication) throws StripeException {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        ensureInvoiceOwner(invoice, authentication);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return "redirect:/invoices/view/" + invoice.getBooking().getId();
        }

        Session session = paymentService.createCheckoutSession(invoice, authentication.getName());
        invoiceService.saveCheckoutSession(invoice.getId(), session.getId());
        return "redirect:" + session.getUrl();
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(@RequestParam(required = false, name = "session_id") String sessionId,
                                 Authentication authentication,
                                 Model model) {
        if (sessionId != null && !sessionId.isBlank()) {
            Invoice invoice = invoiceService.getInvoiceBySessionId(sessionId);
            ensureInvoiceOwner(invoice, authentication);
            model.addAttribute("invoice", invoice);
        }

        return "payment-success";
    }

    @GetMapping("/payments/cancel")
    public String paymentCancel(@RequestParam Long invoiceId, Authentication authentication, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        ensureInvoiceOwner(invoice, authentication);
        model.addAttribute("invoice", invoice);
        return "payment-cancel";
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String signatureHeader) {
        var event = paymentService.parseWebhookEvent(payload, signatureHeader);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = paymentService.extractCheckoutSession(event);
            invoiceService.markPaidBySessionId(session.getId(), session.getPaymentIntent());
        }

        return ResponseEntity.ok("received");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleStripeErrors(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    private void ensureInvoiceOwner(Invoice invoice, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication is required");
        }

        String ownerEmail = invoice.getBooking().getCustomerProfile().getUser().getEmail();
        if (!ownerEmail.equals(authentication.getName())) {
            throw new AccessDeniedException("You do not have access to this invoice");
        }
    }
}
