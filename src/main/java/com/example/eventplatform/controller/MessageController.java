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

    // GET /messages?organizerUserId=2&role=customer
    @GetMapping
    public String showMessages(
            @RequestParam(defaultValue = "2") Long organizerUserId,
            @RequestParam(defaultValue = "customer") String role,
            Model model) {

        Long customerUserId = TEMP_CUSTOMER_USER_ID;

        // Always fetch all messages between customer and organizer
        List<Message> messages = messageService
                .getMessagesBetweenUsers(customerUserId, organizerUserId);

        // Sender and receiver swap based on who is viewing
        Long senderId;
        Long receiverId;

        if (role.equals("organizer")) {
            senderId = organizerUserId;
            receiverId = customerUserId;
        } else {
            senderId = customerUserId;
            receiverId = organizerUserId;
        }

        model.addAttribute("messages", messages);
        model.addAttribute("organizerUserId", organizerUserId);
        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("role", role);

        return "messages";
    }

    // POST /messages/send
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam Long organizerUserId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam String role) {

        messageService.sendMessage(senderId, receiverId, content);

        return "redirect:/messages?organizerUserId="
                + organizerUserId + "&role=" + role;
    }
}
