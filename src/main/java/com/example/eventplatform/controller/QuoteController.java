package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Quote;
import com.example.eventplatform.entity.QuoteStatus;
import com.example.eventplatform.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    // Show all quotes for an organizer
    @GetMapping("/organizer/{organizerId}")
    public String getQuotesByOrganizer(
            @PathVariable Long organizerId,
            Model model) {
        List<Quote> quotes = quoteService.getQuotesByOrganizer(organizerId);
        model.addAttribute("quotes", quotes);
        model.addAttribute("organizerId", organizerId);
        return "quotes";
    }

    // Create a new quote
    @PostMapping("/create")
    public String createQuote(
            @RequestParam String planName,
            @RequestParam BigDecimal price,
            @RequestParam String description
    ) {
        quoteService.createQuote(planName, price, description);
        return "redirect:/quotes/organizer/1";
    }

    // Update quote status (ACCEPT / REJECT)
    @PostMapping("/update-status")
    public String updateQuoteStatus(
            @RequestParam Long quoteId,
            @RequestParam QuoteStatus status,
            @RequestParam Long organizerId
    ) {
        quoteService.updateQuoteStatus(quoteId, status);
        return "redirect:/quotes/organizer/" + organizerId;
    }
}