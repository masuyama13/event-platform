package com.example.eventplatform.controller;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ContactControllerTest {

    @Test
    void organizerContactPage_shouldRenderForOrganizer() {
        ContactController controller = new ContactController("support@event.test");
        Model model = mock(Model.class);
        UserPrincipal principal = UserPrincipal.from(user(1L, "organizer@test.com", UserRole.ORGANIZER));

        String view = controller.organizerContactPage(principal, model);

        assertThat(view).isEqualTo("organizer/contact");
        verify(model).addAttribute("organizerEmail", "organizer@test.com");
        verify(model).addAttribute("supportEmail", "support@event.test");
    }

    @Test
    void customerContactPage_shouldRedirectForOrganizerUser() {
        ContactController controller = new ContactController("support@event.test");
        Model model = mock(Model.class);
        UserPrincipal principal = UserPrincipal.from(user(2L, "organizer@test.com", UserRole.ORGANIZER));

        String view = controller.customerContactPage(principal, model);

        assertThat(view).isEqualTo("redirect:/");
    }

    private User user(Long id, String email, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash("secret");
        return user;
    }
}
