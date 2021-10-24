package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user_category.UserCategory;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> getCategories();
    List<String> getCategoryNamesFromCategories(List<Category> categories);
    List<Category> getUserCategories(String token);
    Map<String, List<UserCategory>> compareCategories(List<String> categories, List<UserCategory> dbUserCategories, User user);
    void updateUserCategory(String token, Map<String, List<UserCategory>> userCategories) throws Exception;
}
