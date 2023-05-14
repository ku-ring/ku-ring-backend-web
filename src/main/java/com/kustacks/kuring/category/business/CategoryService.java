package com.kustacks.kuring.category.business;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.category.exception.CategoryNotFoundException;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserCategory;
import com.kustacks.kuring.user.domain.UserCategoryRepository;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CategoryService {

    private static final String NEW_CATEGORY_FLAG = "new";
    private static final String REMOVE_CATEGORY_FLAG = "remove";

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final Map<String, Category> categoryMap;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository, UserCategoryRepository userCategoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.categoryMap = categoryRepository.findAllMap();
    }

    @Transactional(readOnly = true)
    public List<CategoryName> lookUpSupportedCategories() {
        return categoryRepository.getSupportedCategoryNames();
    }

    @Transactional(readOnly = true)
    public List<CategoryName> lookUpUserCategories(String token) {
        return userCategoryRepository.getUserCategoryNamesByToken(token);
    }

    public Map<String, List<UserCategory>> compareSubscribeCategory(String token, List<String> newCategoryNames) {
        User user = findUserByToken(token);
        List<UserCategory> oldUserCategories = userCategoryRepository.getUserCategoriesByToken(token);
        return compareUserCategory(user, oldUserCategories, verifyCategories(newCategoryNames));
    }

    public void saveUserCategory(UserCategory userCategory) {
        userCategoryRepository.save(userCategory);
    }

    public void deleteUserCategory(UserCategory userCategory) {
        userCategoryRepository.delete(userCategory);
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userRepository.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userRepository.save(new User(token)));
        }

        return optionalUser.orElseThrow(UserNotFoundException::new);
    }

    private List<String> verifyCategories(List<String> categories) {
        validationSupportedCategory(categories);

        return categories.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void validationSupportedCategory(List<String> categoryNames) {
        for (String category : categoryNames) {
            if (categoryMap.get(category) == null) {
                throw new CategoryNotFoundException();
            }
        }
    }

    private List<String> convertNameList(List<UserCategory> userCategoryList) {
        return userCategoryList.stream()
                .map(UserCategory::getCategoryName)
                .collect(Collectors.toList());
    }

    private Map<String, List<UserCategory>> compareUserCategory(User user, List<UserCategory> oldUserCategories, List<String> newCategoryNames) {
        Map<String, List<UserCategory>> result = new HashMap<>();
        List<UserCategory> newList = new ArrayList<>();
        List<UserCategory> removeList = new ArrayList<>();
        List<String> oldCategoryNames = convertNameList(oldUserCategories);

        for (String newCategoryName : newCategoryNames) {
            if (!oldCategoryNames.contains(newCategoryName)) {
                newList.add(new UserCategory(user, categoryMap.get(newCategoryName)));
            }
        }

        for (UserCategory oldUserCategory : oldUserCategories) {
            if (!newCategoryNames.contains(oldUserCategory.getCategoryName())) {
                removeList.add(oldUserCategory);
            }
        }

        result.put(NEW_CATEGORY_FLAG, newList);
        result.put(REMOVE_CATEGORY_FLAG, removeList);
        return result;
    }
}
