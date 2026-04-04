package com.example.eventplatform.service;

import com.example.eventplatform.entity.Category;
import com.example.eventplatform.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Set<Category> getCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new RuntimeException("Please select at least one valid category.");
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != new LinkedHashSet<>(categoryIds).size()) {
            throw new RuntimeException("Please select at least one valid category.");
        }

        return new LinkedHashSet<>(categories);
    }
}
