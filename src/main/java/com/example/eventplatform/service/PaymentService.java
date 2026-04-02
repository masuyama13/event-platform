package com.example.eventplatform.service;

import com.example.eventplatform.entity.Invoice;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final String stripeSecretKey;
    private final String stripeWebhookSecret;
    private final String appBaseUrl;

    public PaymentService(@Value("${stripe.secret-key}") String stripeSecretKey,
                          @Value("${stripe.webhook-secret}") String stripeWebhookSecret,
                          @Value("${app.base-url}") String appBaseUrl) {
        this.stripeSecretKey = stripeSecretKey;
        this.stripeWebhookSecret = stripeWebhookSecret;
        this.appBaseUrl = appBaseUrl;
    }

    public Session createCheckoutSession(Invoice invoice, String customerEmail) throws StripeException {
        requireSecretKey();
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(appBaseUrl + "/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(appBaseUrl + "/payments/cancel?invoiceId=" + invoice.getId())
                .setCustomerEmail(customerEmail)
                .setClientReferenceId(invoice.getId().toString())
                .putMetadata("invoiceId", invoice.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(invoice.getCurrency())
                                                .setUnitAmount(toStripeAmount(invoice.getTotalAmount()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Booking #" + invoice.getBooking().getId())
                                                                .setDescription(invoice.getBooking().getPlanName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }

    public Event parseWebhookEvent(String payload, String signatureHeader) {
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }

        try {
            return Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", e);
        }
    }

    public Session extractCheckoutSession(Event event) {
        var dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (stripeObject == null) {
            try {
                stripeObject = dataObjectDeserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new IllegalStateException("Unable to deserialize Stripe event payload", e);
            }
        }
        if (stripeObject == null) {
            throw new IllegalStateException("Unable to deserialize Stripe event payload");
        }

        return (Session) stripeObject;
    }

    private void requireSecretKey() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("Stripe secret key is not configured");
        }
    }

    private long toStripeAmount(BigDecimal amount) {
        return amount.movePointRight(2).longValueExact();
    }
}
