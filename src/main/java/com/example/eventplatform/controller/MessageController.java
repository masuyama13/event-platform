package com.example.eventplatform.controller;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.security.UserPrincipal;
import com.example.eventplatform.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // ─────────────────────────────────────────────
    // ORGANIZER: Inbox - list of all conversations
    // GET /messages/inbox
    // ─────────────────────────────────────────────
    @GetMapping("/inbox")
    public String showInbox(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        Long organizerUserId = principal.getUserId();

        List<Map<String, Object>> conversations =
                messageService.getConversationListForOrganizer(organizerUserId);

        model.addAttribute("conversations", conversations);

        return "message-inbox";
    }

    // ─────────────────────────────────────────────
    // ORGANIZER: View conversation with a customer
    // GET /messages/conversation/{customerUserId}
    // ─────────────────────────────────────────────
    @GetMapping("/conversation/{customerUserId}")
    public String showConversationForOrganizer(
            @PathVariable Long customerUserId,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        Long organizerUserId = principal.getUserId();

        List<Message> messages =
                messageService.getMessagesBetweenUsers(organizerUserId, customerUserId);

        messageService.markAsRead(organizerUserId, customerUserId);

        // Get customer first name
        String customerFirstName = customerProfileRepository
                .findByUserId(customerUserId)
                .map(CustomerProfile::getFirstName)
                .orElse("Customer");

        // Get organizer business name
        String organizerBusinessName = organizerProfileRepository
                .findByUserId(organizerUserId)
                .map(OrganizerProfile::getBusinessName)
                .orElse("Organizer");

        model.addAttribute("messages", messages);
        model.addAttribute("currentUserId", organizerUserId);
        model.addAttribute("otherUserId", customerUserId);
        model.addAttribute("currentUserName", organizerBusinessName);
        model.addAttribute("otherUserName", customerFirstName);
        model.addAttribute("role", "organizer");

        return "messages";
    }

    // ─────────────────────────────────────────────
    // CUSTOMER: View conversation with an organizer
    // GET /messages?organizerUserId=2
    // ─────────────────────────────────────────────
    @GetMapping
    public String showConversationForCustomer(
            @RequestParam Long organizerUserId,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        Long customerUserId = principal.getUserId();

        List<Message> messages =
                messageService.getMessagesBetweenUsers(customerUserId, organizerUserId);

        messageService.markAsRead(customerUserId, organizerUserId);

        // Get customer first name
        String customerFirstName = customerProfileRepository
                .findByUserId(customerUserId)
                .map(CustomerProfile::getFirstName)
                .orElse("Customer");

        // Get organizer business name
        String organizerBusinessName = organizerProfileRepository
                .findByUserId(organizerUserId)
                .map(OrganizerProfile::getBusinessName)
                .orElse("Organizer");

        model.addAttribute("messages", messages);
        model.addAttribute("currentUserId", customerUserId);
        model.addAttribute("otherUserId", organizerUserId);
        model.addAttribute("currentUserName", customerFirstName);
        model.addAttribute("otherUserName", organizerBusinessName);
        model.addAttribute("role", "customer");

        return "messages";
    }

    // ─────────────────────────────────────────────
    // BOTH: Send a message
    // POST /messages/send
    // ─────────────────────────────────────────────
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam Long otherUserId,
            @RequestParam String content,
            @RequestParam String role,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long currentUserId = principal.getUserId();

        messageService.sendMessage(currentUserId, otherUserId, content);

        // Redirect back to the correct view
        if (role.equals("organizer")) {
            return "redirect:/messages/conversation/" + otherUserId;
        } else {
            return "redirect:/messages?organizerUserId=" + otherUserId;
        }
    }

    // ─────────────────────────────────────────────
// CUSTOMER: Inbox - list of all conversations
// GET /messages/customer-inbox
// ─────────────────────────────────────────────
    @GetMapping("/customer-inbox")
    public String showCustomerInbox(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        Long customerUserId = principal.getUserId();

        List<Map<String, Object>> conversations =
                messageService.getConversationListForCustomer(customerUserId);

        model.addAttribute("conversations", conversations);

        return "message-customer-inbox";
    }
}