package com.kustacks.kuring.category.business;

import com.kustacks.kuring.category.business.event.Events;
import com.kustacks.kuring.category.business.event.SubscribedRollbackEvent;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.category.exception.CategoryNotFoundException;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.common.firebase.exception.FirebaseUnSubscribeException;
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
    private final FirebaseService firebaseService;
    private final Map<String, Category> categoryMap;

    public CategoryService(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            UserCategoryRepository userCategoryRepository,
            FirebaseService firebaseService) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.firebaseService = firebaseService;
        this.categoryMap = categoryRepository.findAllMap();
    }

    @Transactional(readOnly = true)
    public List<CategoryName> lookUpSupportedCategories() {
        return categoryRepository.getSupportedCategoryNames();
    }

    @Transactional(readOnly = true)
    public List<String> lookUpUserCategories(String token) {
        return userCategoryRepository.getUserCategoryNamesByToken(token);
    }

    public void editSubscribeCategoryList(String token, List<String> newCategoryNames) {
        User user = findUserByToken(token);
        List<UserCategory> oldUserCategories = userCategoryRepository.getUserCategoriesByToken(token);
        Map<String, List<UserCategory>> compareResults = compareUserCategory(user, oldUserCategories, verifyCategories(newCategoryNames));
        editUserCategoryList(user, compareResults);
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userRepository.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userRepository.save(new User(token)));
        }

        return optionalUser.orElseThrow(UserNotFoundException::new);
    }

    private void editUserCategoryList(User user, Map<String, List<UserCategory>> compareResult) {
        try {
            String token = user.getToken();

            SubscribedRollbackEvent subscribedRollbackEvent = new SubscribedRollbackEvent(token);
            Events.raise(subscribedRollbackEvent);

            subscribeUserCategory(compareResult, token, subscribedRollbackEvent);
            unsubscribeUserCategory(compareResult, token, subscribedRollbackEvent);
        } catch (FirebaseSubscribeException | FirebaseUnSubscribeException e) {
            throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
        }
    }

    private void subscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, SubscribedRollbackEvent subscribedRollbackEvent) {
        List<UserCategory> newUserCategories = compareResult.get(NEW_CATEGORY_FLAG);
        for (UserCategory newUserCategory : newUserCategories) {
            firebaseService.subscribe(token, newUserCategory.getCategoryName());
            userCategoryRepository.save(newUserCategory);
            subscribedRollbackEvent.addNewCategoryName(newUserCategory.getCategoryName());
            log.info("구독 성공 = {}", newUserCategory.getCategoryName());
        }
    }

    private void unsubscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, SubscribedRollbackEvent subscribedRollbackEvent) {
        List<UserCategory> removeUserCategories = compareResult.get(REMOVE_CATEGORY_FLAG);
        for (UserCategory removeUserCategory : removeUserCategories) {
            firebaseService.unsubscribe(token, removeUserCategory.getCategoryName());
            userCategoryRepository.delete(removeUserCategory);
            subscribedRollbackEvent.deleteNewCategoryName(removeUserCategory.getCategoryName());
            log.info("구독 취소 = {}", removeUserCategory.getCategoryName());
        }
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
