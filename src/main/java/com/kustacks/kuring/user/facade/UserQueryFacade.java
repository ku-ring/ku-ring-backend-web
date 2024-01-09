package com.kustacks.kuring.user.facade;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.user.business.FeedbackService;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.common.dto.CategoryNameDto;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.business.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryFacade {

    private final FeedbackService feedbackService;
    private final UserService userService;
    private final FirebaseService firebaseService;

    public List<CategoryNameDto> lookupSubscribeCategories(String userToken) {
        firebaseService.validationToken(userToken);
        return convertCategoryNameDtoList(userService.lookUpUserCategories(userToken));
    }

    public List<DepartmentNameDto> lookupSubscribeDepartments(String userToken) {
        firebaseService.validationToken(userToken);
        return convertDepartmentDtoList(userService.lookupSubscribeDepartmentList(userToken));
    }

    public List<FeedbackDto> lookupFeedbacks(int page, int size) {
        return feedbackService.lookupFeedbacks(page, size);
    }

    private List<CategoryNameDto> convertCategoryNameDtoList(List<CategoryName> categoryNamesList) {
        return categoryNamesList.stream()
                .map(CategoryNameDto::from)
                .collect(Collectors.toList());
    }

    private List<DepartmentNameDto> convertDepartmentDtoList(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .map(DepartmentNameDto::from)
                .collect(Collectors.toList());
    }
}
