package com.example.eventplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityLoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminLoginPage_shouldRejectCustomerCredentials() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                        .user("customer@test.com")
                        .password("temp1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void adminLoginPage_shouldAllowAdminCredentials() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                        .user("admin@test.com")
                        .password("temp1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("admin@test.com"));
    }

    @Test
    void userLoginPage_shouldRejectAdminCredentials() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("admin@test.com")
                        .password("temp1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void userLoginPage_shouldAllowOrganizerCredentials() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("organizer@test.com")
                        .password("temp1111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("organizer@test.com"));
    }
}
