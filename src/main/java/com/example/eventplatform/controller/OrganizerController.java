package com.example.eventplatform.controller;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.service.CategoryService;
import com.example.eventplatform.service.OrganizerService;
import com.example.eventplatform.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final ReviewService reviewService;
    private final CategoryService categoryService;

    public OrganizerController(OrganizerService organizerService,
                               ReviewService reviewService,
                               CategoryService categoryService) {
        this.organizerService = organizerService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showOrganizerList(@RequestParam(required = false) Long categoryId, Model model) {
        List<OrganizerProfile> sponsoredOrganizers = organizerService.getSponsoredOrganizers(categoryId);
        List<OrganizerProfile> organizers = organizerService.getAllOrganizers(categoryId);
        model.addAttribute("sponsoredOrganizers", sponsoredOrganizers);
        model.addAttribute("organizers", organizers);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        return "organizer-list";
    }

    @GetMapping("/{id}")
    public String showOrganizerDetail(@PathVariable Long id, Model model) {
        OrganizerProfile organizer = organizerService.getOrganizerById(id);
        model.addAttribute("organizer", organizer);
        model.addAttribute("reviews", reviewService.getReviewsByOrganizer(id));
        return "organizer-detail";
    }

    @PostMapping
    public String createOrganizer(@RequestParam Long userId,
                                  @RequestParam String businessName,
                                  @RequestParam String description,
                                  @RequestParam(name = "categoryIds") List<Long> categoryIds,
                                  @RequestParam String phone,
                                  @RequestParam String website,
                                  @RequestParam String address,
                                  Model model) {
        try {
            organizerService.createOrganizer(
                    userId,
                    businessName,
                    description,
                    categoryIds,
                    phone,
                    website,
                    address
            );
            return "redirect:/organizers";
        } catch (RuntimeException e) {
            model.addAttribute("organizer", new OrganizerProfile());
            model.addAttribute("isEdit", false);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "organizer-form";
        }
    }
}
