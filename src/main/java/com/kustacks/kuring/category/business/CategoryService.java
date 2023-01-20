package com.kustacks.kuring.category.business;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.common.dto.CategoryDto;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.domain.UserCategory;
import com.kustacks.kuring.user.domain.UserCategoryRepository;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.category.business.event.RollbackEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {

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

        categoryMap = categoryRepository.findAllMap();
    }


    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }


    public List<CategoryDto> getCategoryDTOList() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryDto(category.getName()))
                .collect(Collectors.toList());
    }

    public List<String> getCategoryNamesFromCategories(List<Category> categories) {

        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    public List<Category> getUserCategories(String token) {

        User user = userRepository.findByToken(token);
        List<UserCategory> userCategories = userCategoryRepository.findAllByUser(user);

        return userCategories.stream()
                .map(UserCategory::getCategory)
                .collect(Collectors.toList());
    }

    public Map<String, List<UserCategory>> compareCategories(List<String> categories, List<UserCategory> dbUserCategories, User user) {

        Map<String, List<UserCategory>> result = new HashMap<>();

        Map<String, UserCategory> dbUserCategoriesMap = listToMap(dbUserCategories);
        Iterator<String> iterator = categories.iterator();

        List<UserCategory> newUserCategories = new LinkedList<>();
        while(iterator.hasNext()) {
            String categoryName = iterator.next();
            UserCategory userCategory = dbUserCategoriesMap.get(categoryName);
            if(userCategory != null) {
                iterator.remove();
                dbUserCategoriesMap.remove(categoryName);
            } else {
                newUserCategories.add(new UserCategory(user, categoryMap.get(categoryName)));
            }
        }

        result.put("new", newUserCategories);
        result.put("remove", new ArrayList<>(dbUserCategoriesMap.values()));

        return result;
    }
    
    // TODO: FirebaseMessagingException 외에 다른 예외 발생할 수 있는지 확인
    @Transactional
    public void updateUserCategory(String token, Map<String, List<UserCategory>> userCategories) throws FirebaseMessagingException {

        Map<String, List<UserCategory>> transactionHistory = new HashMap<>();
        transactionHistory.put("new", new LinkedList<>());
        transactionHistory.put("remove", new LinkedList<>());

        applicationEventPublisher.publishEvent(new RollbackEvent(token, transactionHistory));

        List<UserCategory> newUserCategories = userCategories.get("new");
        for (UserCategory newUserCategory : newUserCategories) {
            firebaseService.subscribe(newUserCategory.getUser().getToken(), newUserCategory.getCategory().getName());
            userCategoryRepository.save(newUserCategory);
            transactionHistory.get("new").add(newUserCategory);
            log.info("구독 요청 = {}", newUserCategory.getCategory().getName());
        }

        List<UserCategory> removeUserCategories = userCategories.get("remove");
        for (UserCategory removeUserCategory : removeUserCategories) {
            firebaseService.unsubscribe(removeUserCategory.getUser().getToken(), removeUserCategory.getCategory().getName());
            userCategoryRepository.delete(removeUserCategory);
            transactionHistory.get("remove").add(removeUserCategory);
            log.info("구독 취소 = {}", removeUserCategory.getCategory().getName());
        }
    }

    public List<String> verifyCategories(List<String> categories) {
        
        // 카테고리 지원 여부 검사
        for (String category : categories) {
            if(categoryMap.get(category) == null) {
                throw new InternalLogicException(ErrorCode.CAT_NOT_EXIST_CATEGORY);
            }
        }
        
        // 카테고리 이름 중복 검사
        HashSet<String> set = new HashSet<>(categories);
        return new ArrayList<>(set);
    }

    private Map<String, UserCategory> listToMap(List<UserCategory> userCategories) {

        Map<String, UserCategory> map = new HashMap<>();
        for (UserCategory userCategory : userCategories) {
            map.put(userCategory.getCategory().getName(), userCategory);
        }

        return map;
    }
}
