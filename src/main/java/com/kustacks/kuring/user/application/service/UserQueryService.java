package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.message.firebase.ServerProperties;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.firebase.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserQueryService implements UserQueryUseCase {

    private final UserPersistenceAdapter userPersistenceAdapter;
    private final NoticeRepository noticeRepository;
    private final FirebaseService firebaseService;
    private final ServerProperties serverProperties;

    @Override
    public List<UserCategoryNameResult> lookupSubscribeCategories(String userToken) {
        firebaseService.validationToken(userToken);
        User findUser = findUserByToken(userToken);
        return convertCategoryNameDtoList(findUser.getSubscribedCategoryList());
    }

    @Override
    public List<UserDepartmentNameResult> lookupSubscribeDepartments(String userToken) {
        firebaseService.validationToken(userToken);
        User findUser = findUserByToken(userToken);
        return convertDepartmentDtoList(findUser.getSubscribedDepartmentList());
    }

    @Override
    public List<AdminFeedbacksResult> lookupFeedbacks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userPersistenceAdapter.findAllFeedbackByPageRequest(pageRequest)
                .stream()
                .map(AdminFeedbacksResult::from)
                .toList();
    }

    @Override
    public List<UserBookmarkResult> lookupUserBookmarkedNotices(String userToken) {
        User user = findUserByToken(userToken);
        List<String> bookmarkIds = user.lookupAllBookmarkIds();

        return noticeRepository.findAllByBookmarkIds(bookmarkIds)
                .stream()
                .map(dto -> new UserBookmarkResult(
                        dto.getArticleId(),
                        dto.getPostedDate(),
                        dto.getSubject(),
                        dto.getCategory(),
                        dto.getBaseUrl())
                ).toList();
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userPersistenceAdapter.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userPersistenceAdapter.save(new User(token)));
            firebaseService.subscribe(token, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
        }

        return optionalUser.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private List<UserCategoryNameResult> convertCategoryNameDtoList(List<CategoryName> categoryNamesList) {
        return categoryNamesList.stream()
                .map(UserCategoryNameResult::from)
                .toList();
    }

    private List<UserDepartmentNameResult> convertDepartmentDtoList(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .map(UserDepartmentNameResult::from)
                .toList();
    }
}
