package com.kustacks.kuring.category.business;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.admin.common.dto.response.CategoryDto;
import com.kustacks.kuring.category.business.event.RollbackEvent;
import com.kustacks.kuring.category.common.dto.response.CategoryListResponse;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserCategory;
import com.kustacks.kuring.user.domain.UserCategoryRepository;
import com.kustacks.kuring.user.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<String, Category> categoryMap;

    public CategoryService(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            UserCategoryRepository userCategoryRepository,
            ApplicationEventPublisher applicationEventPublisher,
            FirebaseService firebaseService) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.firebaseService = firebaseService;
        this.categoryMap = categoryRepository.findAllMap();
    }

    @Transactional(readOnly = true)
    public CategoryListResponse lookUpSupportedCategories() {
        List<String> categoryNames = getCategoryNamesFromCategories(categoryRepository.findAll());
        return new CategoryListResponse(categoryNames);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoryDTOList() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryDto(category.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryListResponse lookUpUserCategories(String token) {
        List<String> categoryNames = userCategoryRepository.getUserCategoryNamesByToken(token);
        return new CategoryListResponse(categoryNames);
    }

    public void editSubscribeCategoryList(String token, List<String> newCategoryNames) {
        User user = findUserByToken(token);
        List<UserCategory> oldUserCategories = userCategoryRepository.getUserCategoriesByToken(token);
        Map<String, List<UserCategory>> compareResults = compareUserCategory(user, oldUserCategories, verifyCategories(newCategoryNames));
        editUserCategoryList(user, compareResults);
    }

    private User findUserByToken(String token) {
        User user = userRepository.findByToken(token);
        if(user == null) {
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException | InternalLogicException e) {
                throw new APIException(ErrorCode.API_FB_INVALID_TOKEN, e);
            }

            user = userRepository.save(new User(token));
        }
        return user;
    }

    private void editUserCategoryList(User user, Map<String, List<UserCategory>> compareResult) {
        try {
            String token = user.getToken();

            RollbackEvent rollbackEvent = new RollbackEvent(token);
            applicationEventPublisher.publishEvent(rollbackEvent);

            subscribeUserCategory(compareResult, token, rollbackEvent);
            unsubscribeUserCategory(compareResult, token, rollbackEvent);
        } catch (Exception e) {
            if(e instanceof FirebaseMessagingException) {
                throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
            } else {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        }
    }

    private void subscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, RollbackEvent rollbackEvent) throws FirebaseMessagingException {
        List<UserCategory> newUserCategories = compareResult.get(NEW_CATEGORY_FLAG);
        for (UserCategory newUserCategory : newUserCategories) {
            firebaseService.subscribe(token, newUserCategory.getCategoryName());
            userCategoryRepository.save(newUserCategory);
            rollbackEvent.addNewCategoryName(newUserCategory.getCategoryName());
            log.info("구독 성공 = {}", newUserCategory.getCategoryName());
        }
    }

    private void unsubscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, RollbackEvent rollbackEvent) throws FirebaseMessagingException {
        List<UserCategory> removeUserCategories = compareResult.get(REMOVE_CATEGORY_FLAG);
        for (UserCategory removeUserCategory : removeUserCategories) {
            firebaseService.unsubscribe(token, removeUserCategory.getCategoryName());
            userCategoryRepository.delete(removeUserCategory);
            rollbackEvent.deleteNewCategoryName(removeUserCategory.getCategoryName());
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
            if(categoryMap.get(category) == null) {
                throw new APIException(ErrorCode.API_INVALID_PARAM);
            }
        }
    }

    private List<String> getCategoryNamesFromCategories(List<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
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

        for(String newCategoryName : newCategoryNames) {
            if(!oldCategoryNames.contains(newCategoryName)) {
                newList.add(new UserCategory(user, categoryMap.get(newCategoryName)));
            }
        }

        for(UserCategory oldUserCategory : oldUserCategories) {
            if(!newCategoryNames.contains(oldUserCategory.getCategoryName())) {
                removeList.add(oldUserCategory);
            }
        }
        result.put(NEW_CATEGORY_FLAG, newList);
        result.put(REMOVE_CATEGORY_FLAG, removeList);

        return result;
    }
}
