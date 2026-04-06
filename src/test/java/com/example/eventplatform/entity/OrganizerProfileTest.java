package com.example.eventplatform.entity;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizerProfileTest {

    @Test
    void getSortedCategories_shouldReturnCategoriesSortedCaseInsensitively() {
        Category wedding = new Category("Wedding");
        Category birthday = new Category("birthday");
        Category workshop = new Category("Workshop");

        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setCategories(new LinkedHashSet<>(java.util.List.of(wedding, birthday, workshop)));

        assertThat(organizerProfile.getSortedCategoryNames())
                .containsExactly("birthday", "Wedding", "Workshop");
    }

    @Test
    void getSortedCategories_shouldReturnEmptyListWhenCategoriesAreNull() {
        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setCategories(null);

        assertThat(organizerProfile.getSortedCategories()).isEmpty();
        assertThat(organizerProfile.getSortedCategoryNames()).isEmpty();
    }
}
