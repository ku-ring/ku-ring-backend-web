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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
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
        this.categoryMap = categoryRepository.findAllMap();
    }

    @Transactional(readOnly = true)
    public CategoryListResponse lookUpSupportedCategories() {
        List<String> categoryNames = getCategoryNamesFromCategories(getCategories());
        return new CategoryListResponse(categoryNames);
    }

    private List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    private List<String> getCategoryNamesFromCategories(List<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoryDTOList() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryDto(category.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryListResponse lookUpUserCategories(String token) {
        List<String> categoryNames = userCategoryRepository.getUserCategoryNamesByToken(token);
        return new CategoryListResponse(categoryNames);
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

//    private void editUserCategoryList(User user, Map<String, List<String>> compareResult) throws FirebaseMessagingException {
//        String token = user.getToken();
//
//        Map<String, List<String>> transactionHistory = new HashMap<>();
//        transactionHistory.put("new", new LinkedList<>());
//        transactionHistory.put("remove", new LinkedList<>());
//
//        applicationEventPublisher.publishEvent(new RollbackEvent(token, transactionHistory));
//
//        List<String> newCategoryList = compareResult.get("new");
//        for (String newCategoryName : newCategoryList) {
//            firebaseService.subscribe(token, newCategoryName);
//            user.addCategory(categoryMap.get(newCategoryName));
//            transactionHistory.get("new").add(newCategoryName);
//            log.info("구독 요청 = {}", newCategoryName);
//        }
//
//        List<String> removeCategoryList = compareResult.get("remove");
//        for (String removeCategoryName : removeCategoryList) {
//            firebaseService.unsubscribe(token, removeCategoryName);
//            user.deleteCategory(removeCategoryName);
//            transactionHistory.get("remove").add(removeCategoryName);
//            log.info("구독 취소 = {}", removeCategoryName);
//        }
//    }

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

    public void editSubscribeList(String token, List<String> categories) {
        // categories에 중복된 카테고리 검사 & 지원하지 않는 카테고리 검사
        try {
            categories = verifyCategories(categories);
        } catch(InternalLogicException e) {
            throw new APIException(ErrorCode.API_INVALID_PARAM, e);
        }

        // FCM 토큰이 서버에 등록된 토큰인지 확인
        // 등록안되어 있다면 firebase에 유효한 토큰인지 확인 후 유효하다면 DB에 등록
        User user = userRepository.findByToken(token);
        if(user == null) {
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException | InternalLogicException e) {
                throw new APIException(ErrorCode.API_FB_INVALID_TOKEN, e);
            }

            user = userRepository.save(new User(token));
        }

        // 클라이언트가 등록 희망한 카테고리 목록과 DB에 등록되어있는 카테고리 목록을 비교
        // categories에는 있고 dbUserCategories에는 없는 건 새로 구독해야할 카테고리
        // dbUserCategories에는 있고 categories에는 없는 건 구독을 취소해야할 카테고리
        // 두 리스트 모두에 있는 카테고리는 무시. 아무 작업도 안해도 됨
        List<UserCategory> dbUserCategories = user.getUserCategories();
        Map<String, List<UserCategory>> resultMap = compareCategories(categories, dbUserCategories, user);

        // TODO 지우가 진행중
//        List<String> categoryNames = userCategoryRepository.getUserCategoryNamesByToken(token);
//        Map<String, List<String>> compareResult = compareCategoryName(categoryNames, categories);
//        editUserCategoryList(user, compareResult);

        // 새로운 카테고리 구독 및 그렇지 않은 카테고리 구독 취소 작업 (DB, Firebase api 작업)
        // Transactional하게 동작하게 하기 위해 service layer에서 작업
        try {
            updateUserCategory(token, resultMap);
        } catch (Exception e) {
            if(e instanceof FirebaseMessagingException) {
                throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
            } else {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        }
    }

//    private Map<String, List<String>> compareCategoryName(List<String> oldCategoryNames, List<String> newCategoryNames) {
//        Map<String, List<String>> result = new HashMap<>();
//        List<String> newList = new ArrayList<>();
//        List<String> deleteList = new ArrayList<>();
//
//        for(String newCategoryName : newCategoryNames) {
//            if(!oldCategoryNames.contains(newCategoryName)) {
//                newList.add(newCategoryName);
//            }
//        }
//
//        for(String oldCategoryName : oldCategoryNames) {
//            if(!newCategoryNames.contains(oldCategoryName)) {
//                deleteList.add(oldCategoryName);
//            }
//        }
//        result.put("new", newList);
//        result.put("delete", deleteList);
//
//        return result;
//    }
}
