package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.category.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    List<String> getCategoryNamesFromCategories(List<Category> categories);
    List<Category> getUserCategories(String token);
}
