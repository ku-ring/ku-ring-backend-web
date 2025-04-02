package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserAIAskCountResult;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserInfoResult;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserQueryService implements UserQueryUseCase {

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final RootUserQueryPort rootUserQueryPort;
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
        return lookupAllBookmarkByIds(bookmarkIds);
    }

    @Override
    public UserAIAskCountResult lookupUserAIAskCountWithFcmToken(String userToken) {
        User user = findUserByToken(userToken);
        return new UserAIAskCountResult(user.getQuestionCount(), User.FCM_USER_MONTHLY_QUESTION_COUNT);
    }

    @Override
    public UserAIAskCountResult lookupUserAIAskCountWithEmail(String email) {
        RootUser rootUser = findRootUserByEmailOrThrow(email);
        return new UserAIAskCountResult(rootUser.getQuestionCount(), RootUser.ROOT_USER_MONTHLY_QUESTION_COUNT);
    }

    @Override
    public UserInfoResult lookupUserInfo(String email) {
        RootUser rootUser = findRootUserByEmailOrThrow(email);
        return new UserInfoResult(rootUser.getNickname(), rootUser.getEmail());
    }


    private List<UserBookmarkResult> lookupAllBookmarkByIds(List<String> bookmarkIds) {
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

    private RootUser findRootUserByEmailOrThrow(String email) {
        return rootUserQueryPort.findRootUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOT_USER_NOT_FOUND));
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
