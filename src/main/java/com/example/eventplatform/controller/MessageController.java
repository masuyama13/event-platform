package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Message;
import com.example.eventplatform.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // TODO: Replace with real user from authentication later
    private final Long TEMP_CUSTOMER_USER_ID = 1L;
    private final Long TEMP_ORGANIZER_USER_ID = 2L;

    // GET /messages/{bookingId}?role=customer or ?role=organizer
    @GetMapping("/{bookingId}")
    public String showMessages(
            @PathVariable Long bookingId,
            @RequestParam(defaultValue = "customer") String role,
            Model model) {

        List<Message> messages = messageService
                .getMessagesByBooking(bookingId);

        // TODO: Replace with real user from authentication later
        // Switch sender and receiver based on role
        Long senderId;
        Long receiverId;

        if (role.equals("organizer")) {
            senderId = TEMP_ORGANIZER_USER_ID;
            receiverId = TEMP_CUSTOMER_USER_ID;
        } else {
            senderId = TEMP_CUSTOMER_USER_ID;
            receiverId = TEMP_ORGANIZER_USER_ID;
        }

        // Mark messages as read for current user
        messageService.markAsRead(bookingId, senderId);

        model.addAttribute("messages", messages);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("role", role);

        return "messages";
    }

    // POST /messages/send
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam Long bookingId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam String role) {

        messageService.sendMessage(
                bookingId, senderId, receiverId, content
        );

        // Redirect back to conversation with same role
        return "redirect:/messages/" + bookingId + "?role=" + role;
    }
}
