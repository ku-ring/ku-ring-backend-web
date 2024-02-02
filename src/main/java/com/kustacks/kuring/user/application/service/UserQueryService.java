package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.application.service.ServerProperties;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.application.service.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserQueryService implements UserQueryUseCase {

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final NoticeQueryPort noticeQueryPort;
    private final UserEventPort userEventPort;
    private final ServerProperties serverProperties;

    @Override
    public List<UserCategoryNameResult> lookupSubscribeCategories(String userToken) {
        User findUser = findUserByToken(userToken);
        return convertCategoryNameDtoList(findUser.getSubscribedCategoryList());
    }

    @Override
    public List<UserDepartmentNameResult> lookupSubscribeDepartments(String userToken) {
        User findUser = findUserByToken(userToken);
        return convertDepartmentDtoList(findUser.getSubscribedDepartmentList());
    }

    @Override
    public List<UserBookmarkResult> lookupUserBookmarkedNotices(String userToken) {
        User user = findUserByToken(userToken);
        List<String> bookmarkIds = user.lookupAllBookmarkIds();

        return noticeQueryPort.findAllByBookmarkIds(bookmarkIds)
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
        Optional<User> optionalUser = userQueryPort.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userCommandPort.save(new User(token)));
            userEventPort.subscribeEvent(token, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
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
