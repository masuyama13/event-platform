package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Quote;
import com.example.eventplatform.entity.QuoteStatus;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository mockQuoteRepository;
    @Mock
    private OrganizerProfileRepository mockOrganizerProfileRepository;

    @InjectMocks
    private QuoteService quoteServiceUnderTest;

    @Test
    void testCreateQuote() {
        // Setup
        // Configure OrganizerProfileRepository.findAll(...).
        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        organizerProfile.setUser(user);
        final List<OrganizerProfile> organizerProfiles = List.of(organizerProfile);
        when(mockOrganizerProfileRepository.findAll()).thenReturn(organizerProfiles);

        // Configure QuoteRepository.save(...).
        final Quote quote = new Quote();
        final OrganizerProfile organizer = new OrganizerProfile();
        quote.setOrganizer(organizer);
        quote.setPlanName("planName");
        quote.setDescription("description");
        quote.setPrice(new BigDecimal("0.00"));
        quote.setStatus(QuoteStatus.PENDING);
        quote.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockQuoteRepository.save(any(Quote.class))).thenReturn(quote);

        // Run the test
        final Quote result = quoteServiceUnderTest.createQuote("planName", new BigDecimal("0.00"), "description");

        // Verify the results
    }

    @Test
    void testCreateQuote_OrganizerProfileRepositoryReturnsNoItems() {
        // Setup
        when(mockOrganizerProfileRepository.findAll()).thenReturn(Collections.emptyList());

        // Configure QuoteRepository.save(...).
        final Quote quote = new Quote();
        final OrganizerProfile organizer = new OrganizerProfile();
        quote.setOrganizer(organizer);
        quote.setPlanName("planName");
        quote.setDescription("description");
        quote.setPrice(new BigDecimal("0.00"));
        quote.setStatus(QuoteStatus.PENDING);
        quote.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockQuoteRepository.save(any(Quote.class))).thenReturn(quote);

        // Run the test
        final Quote result = quoteServiceUnderTest.createQuote("planName", new BigDecimal("0.00"), "description");

        // Verify the results
    }

    @Test
    void testGetQuotesByOrganizer() {
        // Setup
        // Configure QuoteRepository.findByOrganizerId(...).
        final Quote quote = new Quote();
        final OrganizerProfile organizer = new OrganizerProfile();
        quote.setOrganizer(organizer);
        quote.setPlanName("planName");
        quote.setDescription("description");
        quote.setPrice(new BigDecimal("0.00"));
        quote.setStatus(QuoteStatus.PENDING);
        quote.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final List<Quote> quotes = List.of(quote);
        when(mockQuoteRepository.findByOrganizerId(0L)).thenReturn(quotes);

        // Run the test
        final List<Quote> result = quoteServiceUnderTest.getQuotesByOrganizer(0L);

        // Verify the results
    }

    @Test
    void testGetQuotesByOrganizer_QuoteRepositoryReturnsNoItems() {
        // Setup
        when(mockQuoteRepository.findByOrganizerId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Quote> result = quoteServiceUnderTest.getQuotesByOrganizer(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testUpdateQuoteStatus() {
        // Setup
        // Configure QuoteRepository.findById(...).
        final Quote quote1 = new Quote();
        final OrganizerProfile organizer = new OrganizerProfile();
        quote1.setOrganizer(organizer);
        quote1.setPlanName("planName");
        quote1.setDescription("description");
        quote1.setPrice(new BigDecimal("0.00"));
        quote1.setStatus(QuoteStatus.PENDING);
        quote1.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final Optional<Quote> quote = Optional.of(quote1);
        when(mockQuoteRepository.findById(0L)).thenReturn(quote);

        // Run the test
        quoteServiceUnderTest.updateQuoteStatus(0L, QuoteStatus.PENDING);

        // Verify the results
        verify(mockQuoteRepository).save(any(Quote.class));
    }

    @Test
    void testUpdateQuoteStatus_QuoteRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockQuoteRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> quoteServiceUnderTest.updateQuoteStatus(0L, QuoteStatus.PENDING))
                .isInstanceOf(RuntimeException.class);
    }
}
