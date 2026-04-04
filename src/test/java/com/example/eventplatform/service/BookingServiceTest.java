package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CustomerProfileRepository mockCustomerProfileRepository;
    @Mock
    private OrganizerProfileRepository mockOrganizerProfileRepository;
    @Mock
    private QuoteRepository mockQuoteRepository;

    @InjectMocks
    private BookingService bookingServiceUnderTest;

    @Test
    void testGetAvailablePlans() {
        // Setup
        // Configure QuoteRepository.findByStatus(...).
        final Quote quote = new Quote();
        quote.setId(0L);
        final OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(0L);
        final User user = new User();
        organizer.setUser(user);
        quote.setOrganizer(organizer);
        quote.setPlanName("planName");
        final List<Quote> quotes = List.of(quote);
        when(mockQuoteRepository.findByStatus(QuoteStatus.PENDING)).thenReturn(quotes);

        // Run the test
        final List<Quote> result = bookingServiceUnderTest.getAvailablePlans();

        // Verify the results
    }

    @Test
    void testGetAvailablePlans_QuoteRepositoryReturnsNoItems() {
        // Setup
        when(mockQuoteRepository.findByStatus(QuoteStatus.PENDING)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Quote> result = bookingServiceUnderTest.getAvailablePlans();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetPlanDetail() {
        // Setup
        // Configure QuoteRepository.findByStatus(...).
        final Quote quote = new Quote();
        quote.setId(0L);
        final OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(0L);
        final User user = new User();
        organizer.setUser(user);
        quote.setOrganizer(organizer);
        quote.setPlanName("planName");
        final List<Quote> quotes = List.of(quote);
        when(mockQuoteRepository.findByStatus(QuoteStatus.PENDING)).thenReturn(quotes);

        // Run the test
        final Quote result = bookingServiceUnderTest.getPlanDetail("planName");

        // Verify the results
    }

    @Test
    void testGetPlanDetail_QuoteRepositoryReturnsNoItems() {
        // Setup
        when(mockQuoteRepository.findByStatus(QuoteStatus.PENDING)).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(() -> bookingServiceUnderTest.getPlanDetail("planName"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfirmBooking() {
        // Setup
        // Configure CustomerProfileRepository.findAll(...).
        final CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        customerProfile.setUser(user);
        final List<CustomerProfile> customerProfiles = List.of(customerProfile);
        when(mockCustomerProfileRepository.findAll()).thenReturn(customerProfiles);

        // Configure OrganizerProfileRepository.findAll(...).
        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(0L);
        final User user1 = new User();
        user1.setId(0L);
        user1.setEmail("email");
        user1.setPasswordHash("passwordHash");
        organizerProfile.setUser(user1);
        final List<OrganizerProfile> organizerProfiles = List.of(organizerProfile);
        when(mockOrganizerProfileRepository.findAll()).thenReturn(organizerProfiles);

        // Configure BookingRepository.save(...).
        final Booking booking = new Booking();
        final CustomerProfile customerProfile1 = new CustomerProfile();
        booking.setCustomerProfile(customerProfile1);
        final OrganizerProfile organizerProfile1 = new OrganizerProfile();
        booking.setOrganizerProfile(organizerProfile1);
        booking.setEventDate(LocalDate.of(2020, 1, 1));
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setPlannerName("organizerName");
        booking.setPlanName("planName");
        booking.setPrice(new BigDecimal("0.00"));
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Run the test
        final Booking result = bookingServiceUnderTest.confirmBooking("planName", "organizerName",
                LocalDate.of(2020, 1, 1), new BigDecimal("0.00"));

        // Verify the results
    }

    @Test
    void testConfirmBooking_CustomerProfileRepositoryReturnsNoItems() {
        // Setup
        when(mockCustomerProfileRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking("planName", "organizerName", LocalDate.of(2020, 1, 1),
                        new BigDecimal("0.00"))).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfirmBooking_OrganizerProfileRepositoryReturnsNoItems() {
        // Setup
        // Configure CustomerProfileRepository.findAll(...).
        final CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        customerProfile.setUser(user);
        final List<CustomerProfile> customerProfiles = List.of(customerProfile);
        when(mockCustomerProfileRepository.findAll()).thenReturn(customerProfiles);

        when(mockOrganizerProfileRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking("planName", "organizerName", LocalDate.of(2020, 1, 1),
                        new BigDecimal("0.00"))).isInstanceOf(RuntimeException.class);
    }
}
