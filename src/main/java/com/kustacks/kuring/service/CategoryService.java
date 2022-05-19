package com.kustacks.kuring.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.CategoryDTO;
import com.kustacks.kuring.controller.dto.CategoryHierarchyDTO;
import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.persistence.user_category.UserCategory;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<CategoryHierarchyDTO> getSubscribableCategories(String ancestor);
    List<CategoryDTO> getCategoryDTOList();
    List<String> getCategoryNamesFromCategories(List<Category> categories);
    List<Category> getUserCategories(String token);
    Map<String, List<UserCategory>> compareCategories(List<String> categories, List<UserCategory> dbUserCategories, User user);
    void updateUserCategory(String token, Map<String, List<UserCategory>> userCategories) throws FirebaseMessagingException;
    List<String> verifyCategories(List<String> categories);
}
