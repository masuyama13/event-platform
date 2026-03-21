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

    // Show all quotes for a booking
    @GetMapping("/booking/{bookingId}")
    public String getQuotesByBooking(@PathVariable Long bookingId, Model model) {
        List<Quote> quotes = quoteService.getQuotesByBooking(bookingId);
        model.addAttribute("quotes", quotes);
        model.addAttribute("bookingId", bookingId);
        return "quotes"; // → quotes.html (can be created later)
    }

    // Create a new quote
    @PostMapping("/create")
    public String createQuote(
            @RequestParam Long bookingId,
            @RequestParam BigDecimal amount,
            @RequestParam String description
    ) {
        quoteService.createQuote(bookingId, amount, description);
        return "redirect:/quotes/booking/" + bookingId;
    }

    // Update quote status (ACCEPT / REJECT)
    @PostMapping("/update-status")
    public String updateQuoteStatus(
            @RequestParam Long quoteId,
            @RequestParam QuoteStatus status,
            @RequestParam Long bookingId
    ) {
        quoteService.updateQuoteStatus(quoteId, status);
        return "redirect:/quotes/booking/" + bookingId;
    }
}
