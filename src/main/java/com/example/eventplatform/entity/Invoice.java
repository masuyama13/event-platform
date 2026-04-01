package com.example.eventplatform.entity;

import jakarta.persistence.*;
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

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

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

    public Double getTotalAmount() { return totalAmount; }

    public InvoiceStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }

    public void setBooking(Booking booking) { this.booking = booking; }

    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public void setStatus(InvoiceStatus status) { this.status = status; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
