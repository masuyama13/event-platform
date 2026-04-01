package com.example.eventplatform.service;

import java.util.List;

public final class OrganizerCategoryOptions {

    public static final List<String> OPTIONS = List.of(
            "Wedding",
            "Meeting",
            "Conference",
            "Birthday Party",
            "Workshop"
    );

    private OrganizerCategoryOptions() {
    }

    public static boolean isValid(String category) {
        return category != null && OPTIONS.contains(category);
    }
}
