package com.example.eventplatform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency = "cad";

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private String stripeSessionId;
    private String stripePaymentIntentId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Invoice() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters / setters
    public Long getId() { return id; }

    public Booking getBooking() { return booking; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public String getCurrency() { return currency; }

    public InvoiceStatus getStatus() { return status; }

    public String getStripeSessionId() { return stripeSessionId; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }

    public LocalDateTime getPaidAt() { return paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }

    public void setBooking(Booking booking) { this.booking = booking; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public void setCurrency(String currency) { this.currency = currency; }

    public void setStatus(InvoiceStatus status) { this.status = status; }

    public void setStripeSessionId(String stripeSessionId) { this.stripeSessionId = stripeSessionId; }

    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }

    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
