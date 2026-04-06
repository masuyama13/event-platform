package com.example.eventplatform.service;

import com.example.eventplatform.entity.Category;
import com.example.eventplatform.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_shouldReturnSortedCategoriesFromRepository() {
        List<Category> categories = List.of(new Category("Birthday"), new Category("Wedding"));
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).isEqualTo(categories);
    }

    @Test
    void getCategoriesByIds_shouldRejectEmptyInput() {
        assertThatThrownBy(() -> categoryService.getCategoriesByIds(List.of()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please select at least one valid category.");
    }

    @Test
    void getCategoriesByIds_shouldRejectWhenAnyCategoryIsMissing() {
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Category("Wedding")));

        assertThatThrownBy(() -> categoryService.getCategoriesByIds(List.of(1L, 2L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please select at least one valid category.");
    }

    @Test
    void getCategoriesByIds_shouldReturnUniqueCategories() {
        Category wedding = new Category("Wedding");
        Category music = new Category("Music");
        when(categoryRepository.findAllById(List.of(1L, 2L, 1L))).thenReturn(List.of(wedding, music));

        Set<Category> result = categoryService.getCategoriesByIds(List.of(1L, 2L, 1L));

        assertThat(result).containsExactly(wedding, music);
    }
}
