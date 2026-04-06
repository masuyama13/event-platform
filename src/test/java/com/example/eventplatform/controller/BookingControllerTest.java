package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.service.BookingService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletResponse response;

    @Test
    void showBookingRequestForm_shouldPopulatePlanAndCachingHeaders() {
        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(12L);

        Plan plan = new Plan();
        plan.setId(5L);
        plan.setOrganizer(organizer);

        when(bookingService.getPlanDetail(5L)).thenReturn(plan);

        BookingController controller = new BookingController();
        injectBookingService(controller);

        String view = controller.showBookingRequestForm(5L, model, response);

        assertThat(view).isEqualTo("customer/booking-request");
        verify(model).addAttribute("plan", plan);
        verify(model).addAttribute("organizerId", 12L);
        verify(model).addAttribute("earliestBookingDate", LocalDate.now().plusWeeks(1));
        verify(response).setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setDateHeader("Expires", 0);
    }

    @Test
    void submitBookingRequest_shouldRedirectToCompletePage() {
        Booking booking = new Booking();
        booking.setId(99L);

        when(authentication.getName()).thenReturn("customer@test.com");
        when(bookingService.submitBookingRequest(
                7L,
                LocalDate.of(2030, 2, 1),
                "Wedding",
                "Garden party",
                "customer@test.com"
        )).thenReturn(booking);

        BookingController controller = new BookingController();
        injectBookingService(controller);

        RedirectView view = controller.submitBookingRequest(
                7L,
                "2030-02-01",
                "Wedding",
                "Garden party",
                authentication
        );

        assertThat(view.getUrl()).isEqualTo("/booking/complete/99");
    }

    private void injectBookingService(BookingController controller) {
        try {
            Field field = BookingController.class.getDeclaredField("bookingService");
            field.setAccessible(true);
            field.set(controller, bookingService);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
