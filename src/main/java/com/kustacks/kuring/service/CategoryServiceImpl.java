package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.domain.user_category.UserCategory;
import com.kustacks.kuring.domain.user_category.UserCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            UserCategoryRepository userCategoryRepository) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<String> getCategoryNamesFromCategories(List<Category> categories) {

        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getUserCategories(String token) {

        User user = userRepository.findByToken(token);
        List<UserCategory> userCategories = userCategoryRepository.findAllByUser(user);

        return userCategories.stream()
                .map(UserCategory::getCategory)
                .collect(Collectors.toList());
    }
}
