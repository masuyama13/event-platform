package com.example.eventplatform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_profile_id", nullable = false)
    private CustomerProfile customerProfile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_profile_id", nullable = false)
    private OrganizerProfile organizerProfile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    private String eventType;

    private LocalDate eventDate;

    private String location;

    @Column(length = 2000)
    private String requestDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String plannerName;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    public Booking() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CustomerProfile getCustomerProfile() { return customerProfile; }
    public OrganizerProfile getOrganizerProfile() { return organizerProfile; }
    public Plan getPlan() { return plan; }
    public String getEventType() { return eventType; }
    public LocalDate getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public String getRequestDetails() { return requestDetails; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getPlannerName() { return plannerName; }
    public String getPlanName() { return plan != null ? plan.getPlanName() : null; }
    public BigDecimal getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }
    public void setOrganizerProfile(OrganizerProfile organizerProfile) {
        this.organizerProfile = organizerProfile;
    }
    public void setPlan(Plan plan) { this.plan = plan; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public void setLocation(String location) { this.location = location; }
    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setPlannerName(String plannerName) { this.plannerName = plannerName; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
