package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.common.utils.generator.NicknameGenerator;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserAcademicEventNotificationCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDecreaseQuestionCountCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginResult;
import com.kustacks.kuring.user.application.port.in.dto.UserLogoutCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserPasswordModifyCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSignupCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSubscribeCompareResult;
import com.kustacks.kuring.user.application.port.in.dto.UserWithdrawCommand;
import com.kustacks.kuring.user.application.port.out.RootUserCommandPort;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ACADEMIC_EVENT_TOPIC;
import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;
import static com.kustacks.kuring.user.domain.RootUser.ROOT_USER_EXTRA_QUESTION_COUNT;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
class UserCommandService implements UserCommandUseCase {

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final RootUserCommandPort rootUserCommandPort;
    private final RootUserQueryPort rootUserQueryPort;
    private final UserEventPort userEventPort;
    private final ServerProperties serverProperties;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void editSubscribeCategories(UserCategoriesSubscribeCommand command) {
        UserSubscribeCompareResult<CategoryName> compareResults =
                this.editSubscribeCategoryList(command.userToken(), command.categories());

        editUserCategoryList(
                command.userToken(),
                compareResults.savedNameList(),
                compareResults.deletedNameList()
        );
    }

    @Override
    public void editSubscribeDepartments(UserDepartmentsSubscribeCommand command) {
        UserSubscribeCompareResult<DepartmentName> compareResults
                = this.editSubscribeDepartmentList(command.userToken(), command.departments());

        editDepartmentNameList(
                command.userToken(),
                compareResults.savedNameList(),
                compareResults.deletedNameList()
        );
    }

    @Override
    public void updateAcademicEventNotification(UserAcademicEventNotificationCommand command) {
        User user = findUserByToken(command.userToken());
        boolean wasEnabled = user.getAcademicEventNotificationEnabled();

        // 설정이 변경된 경우에만 토픽 구독/해제 처리
        if (wasEnabled != command.enabled()) {
            editAcademicEventNotificationEnabled(
                    command.userToken(),
                    command.enabled()
            );
            user.updateAcademicNotificationEnabled(command.enabled());
        }
    }

    @Override
    public void saveFeedback(UserFeedbackCommand command) {
        User findUser = findUserByToken(command.userToken());
        findUser.addFeedback(command.content());
    }

    @Override
    public void saveBookmark(UserBookmarkCommand command) {
        User user = findUserByToken(command.userToken());
        user.addBookmark(command.articleId());
    }

    @Override
    public void decreaseQuestionCount(UserDecreaseQuestionCountCommand command) {
        User findUser = findUserByToken(command.userId());

        try {
            checkUserLoginIdAndDecreaseCount(findUser, command.email());
        } catch (IllegalStateException e) {
            throw new InvalidStateException(ErrorCode.QUESTION_COUNT_NOT_ENOUGH);
        }
    }

    @Override
    public void signupUser(UserSignupCommand userSignupCommand) {
        String nickname = createNickname();
        RootUser rootUser = new RootUser(
                userSignupCommand.email(),
                passwordEncoder.encode(userSignupCommand.password()),
                nickname
        );

        rootUserQueryPort.findDeletedRootUserByEmail(userSignupCommand.email())
                .ifPresentOrElse(
                        deletedRootUser -> {
                            deletedRootUser.reactive(rootUser.getPassword());
                            log.info("[RootUser name : {}] 재가입 완료", rootUser.getNickname());
                        },
                        () -> {
                            checkDuplicateEmailAndSave(rootUser);
                            log.info("[RootUser name : {}] 가입 완료", rootUser.getNickname());
                        }
                );
    }

    @Override
    public UserLoginResult login(UserLoginCommand userLoginCommand) {
        User user = findUserByToken(userLoginCommand.fcmToken());
        RootUser rootUser = findRootUserByEmailAndPasswordOrThrow(userLoginCommand.email(), userLoginCommand.password());

        checkUserIsNotLoggedIn(user);

        syncQuestionCount(rootUser, user);
        user.login(rootUser.getId());

        String token = jwtTokenProvider.createUserToken(userLoginCommand.email());
        return new UserLoginResult(token);
    }

    @Override
    public void withdraw(UserWithdrawCommand userWithdrawCommand) {
        RootUser rootUser = findRootUserByEmailOrThrow(userWithdrawCommand.email());

        //회원탈퇴 하기 전에 로그인되어 있는 모든 기기들 로그아웃 처리
        logoutAllLoggedInUser(rootUser);

        rootUserCommandPort.deleteRootUser(rootUser);
        log.info("[RootUserId : {}] 삭제 완료", rootUser.getId());
    }

    @Override
    public void changePassword(UserPasswordModifyCommand userPasswordModifyCommand) {
        RootUser rootUser = findRootUserByEmailOrThrow(userPasswordModifyCommand.email());
        rootUser.modifyPassword(passwordEncoder.encode(userPasswordModifyCommand.password()));
    }

    @Override
    public void logout(UserLogoutCommand userLogoutCommand) {
        User user = findUserByToken(userLogoutCommand.fcmToken());
        RootUser rootUser = findRootUserByEmailOrThrow(userLogoutCommand.email());

        checkUserMatchesRootUser(user, rootUser);

        syncQuestionCount(rootUser, user);
        user.logout();
    }

    private void checkUserLoginIdAndDecreaseCount(User user, String email) {
        rootUserQueryPort.findRootUserByEmail(email).ifPresentOrElse(
                rootuser -> {
                    if (matchLoginUserId(user, rootuser)) {
                        rootuser.decreaseQuestionCount();
                    }
                },
                user::decreaseQuestionCount
        );
    }

    private void logoutAllLoggedInUser(RootUser rootUser) {
        userQueryPort.findByLoggedInUserId(rootUser.getId())
                .forEach(User::logout);
    }

    private void checkUserIsNotLoggedIn(User user) {
        if (user.isLoggedIn()) {
            throw new InvalidStateException(ErrorCode.USER_ALREADY_LOGIN);
        }
    }

    private void checkUserMatchesRootUser(User user, RootUser rootUser) {
        if (!matchLoginUserId(user, rootUser)) {
            throw new InvalidStateException(ErrorCode.USER_MISMATCH_DEVICE);
        }
    }

    private boolean matchLoginUserId(User user, RootUser rootuser) {
        return user.matchLoginUserId(rootuser.getId());
    }

    private void syncQuestionCount(RootUser rootUser, User tokenUser) {
        // minQuestionCount = Min(이메일 계정에 남아있는 횟수 - 이메일 계정 추가 횟수, 기기에 남은 횟수)
        int fcmUserQuestionCount = Math.min(rootUser.getQuestionCount() - ROOT_USER_EXTRA_QUESTION_COUNT, tokenUser.getQuestionCount());
        int emailUserQuestionCount = fcmUserQuestionCount + ROOT_USER_EXTRA_QUESTION_COUNT;

        if (fcmUserQuestionCount < 0) {
            fcmUserQuestionCount = 0;
        }

        if (emailUserQuestionCount < 0) {
            emailUserQuestionCount = 0;
        }

        tokenUser.updateQuestionCount(fcmUserQuestionCount);
        rootUser.updateQuestionCount(emailUserQuestionCount);
    }

    private void checkDuplicateEmailAndSave(RootUser rootUser) {
        if (rootUserQueryPort.existRootUserByEmail(rootUser.getEmail())) {
            throw new InvalidStateException(ErrorCode.EMAIL_DUPLICATE);
        }
        rootUserCommandPort.saveRootUser(rootUser);
    }

    private String createNickname() {
        final int BATCH_SIZE = 5;

        while (true) {
            List<String> candidateNicknames = new ArrayList<>();
            for (int i = 0; i < BATCH_SIZE; i++) {
                candidateNicknames.add(NicknameGenerator.generateNickname());
            }

            List<String> usingNicknames = rootUserQueryPort.findUsingNicknamesIn(candidateNicknames);
            List<String> availableNicknames = selectAvailableNicknames(candidateNicknames, usingNicknames);

            // 사용 가능한 닉네임이 있으면 랜덤으로 하나 선택
            if (!availableNicknames.isEmpty()) {
                return availableNicknames.get(0);
            }
        }
    }

    private List<String> selectAvailableNicknames(List<String> candidateNicknames, List<String> usingNicknames) {
        return candidateNicknames.stream()
                .filter(candidateNickname -> !usingNicknames.contains(candidateNickname))
                .toList();
    }

    private UserSubscribeCompareResult<CategoryName> editSubscribeCategoryList(
            String userToken,
            List<String> newCategoryStringNames
    ) {
        User user = findUserByToken(userToken);

        List<CategoryName> newCategoryNames = convertToEnumList(newCategoryStringNames);
        List<CategoryName> savedCategoryNames = user.filteringNewCategoryName(newCategoryNames);
        List<CategoryName> deletedCategoryNames = user.filteringOldCategoryName(newCategoryNames);

        return new UserSubscribeCompareResult<>(savedCategoryNames, deletedCategoryNames);
    }

    private void editUserCategoryList(
            String userToken,
            List<CategoryName> savedCategoryNames,
            List<CategoryName> deletedCategoryNames
    ) throws FirebaseSubscribeException, FirebaseUnSubscribeException {
        subscribeUserCategory(userToken, savedCategoryNames);
        unsubscribeUserCategory(userToken, deletedCategoryNames);
    }

    private UserSubscribeCompareResult<DepartmentName> editSubscribeDepartmentList(
            String userToken,
            List<String> departments
    ) {
        User user = findUserByToken(userToken);

        List<DepartmentName> newDepartmentNames = convertHostPrefixToEnum(departments);
        List<DepartmentName> savedDepartmentNames = user.filteringNewDepartmentName(newDepartmentNames);
        List<DepartmentName> deletedDepartmentNames = user.filteringOldDepartmentName(newDepartmentNames);

        return new UserSubscribeCompareResult<>(savedDepartmentNames, deletedDepartmentNames);
    }

    private void subscribeUserCategory(
            String token,
            List<CategoryName> newCategoryNames
    ) throws FirebaseSubscribeException {
        for (CategoryName newCategoryName : newCategoryNames) {
            userEventPort.subscribeEvent(token, newCategoryName.getName());
            this.subscribeCategory(token, newCategoryName);
            log.debug("구독 성공 = {}", newCategoryName.getName());
        }
    }

    private void unsubscribeUserCategory(
            String token,
            List<CategoryName> removeCategoryNames
    ) throws FirebaseUnSubscribeException {
        for (CategoryName removeCategoryName : removeCategoryNames) {
            userEventPort.unsubscribeEvent(token, removeCategoryName.getName());
            this.unsubscribeCategory(token, removeCategoryName);
            log.debug("구독 취소 = {}", removeCategoryName.getName());
        }
    }

    private void editDepartmentNameList(
            String userToken,
            List<DepartmentName> savedDepartmentNames,
            List<DepartmentName> deletedDepartmentNames
    ) throws FirebaseSubscribeException, FirebaseUnSubscribeException {
        subscribeDepartment(userToken, savedDepartmentNames);
        unsubscribeDepartment(userToken, deletedDepartmentNames);
    }

    private void subscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.subscribeCategory(categoryName);
    }

    private void unsubscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.unsubscribeCategory(categoryName);
    }

    private void subscribeDepartment(
            String userToken,
            List<DepartmentName> newDepartmentNames
    ) {
        for (DepartmentName newDepartmentName : newDepartmentNames) {
            userEventPort.subscribeEvent(userToken, newDepartmentName.getName());
            this.subscribeDepartment(userToken, newDepartmentName);
            log.debug("구독 성공 = {}", newDepartmentName.getName());
        }
    }

    private void unsubscribeDepartment(
            String userToken,
            List<DepartmentName> removeDepartmentNames
    ) {
        for (DepartmentName removeDepartmentName : removeDepartmentNames) {
            userEventPort.unsubscribeEvent(userToken, removeDepartmentName.getName());
            this.unsubscribeDepartment(userToken, removeDepartmentName);
            log.debug("구독 취소 = {}", removeDepartmentName.getName());
        }
    }

    private void subscribeDepartment(String userToken, DepartmentName newDepartmentName) {
        User user = findUserByToken(userToken);
        user.subscribeDepartment(newDepartmentName);
    }

    private void unsubscribeDepartment(String userToken, DepartmentName removeDepartmentName) {
        User user = findUserByToken(userToken);
        user.unsubscribeDepartment(removeDepartmentName);
    }

    private void editAcademicEventNotificationEnabled(String userToken, boolean enabled) {
        if (enabled) {
            // 알림 활성화 -> 토픽 구독
            userEventPort.subscribeEvent(userToken,
                    serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC));
        } else {
            // 알림 비활성화 -> 토픽 구독 해제
            userEventPort.unsubscribeEvent(userToken,
                    serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC));
        }
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userQueryPort.findByToken(token);
        if (optionalUser.isEmpty()) {
            User newUser = new User(token);
            optionalUser = Optional.of(userCommandPort.save(newUser));
            userEventPort.subscribeEvent(token, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
            userEventPort.subscribeEvent(token, serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC));
        }

        return optionalUser.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private List<CategoryName> convertToEnumList(List<String> categories) {
        return categories.stream()
                .map(CategoryName::fromStringName)
                .toList();
    }

    private List<DepartmentName> convertHostPrefixToEnum(List<String> departments) {
        return departments.stream()
                .map(DepartmentName::fromHostPrefix)
                .toList();
    }

    private RootUser findRootUserByEmailOrThrow(String email) {
        return rootUserQueryPort.findRootUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOT_USER_NOT_FOUND));
    }

    private RootUser findRootUserByEmailAndPasswordOrThrow(String email, String password) {
        RootUser rootUser = findRootUserByEmailOrThrow(email);
        if (!passwordEncoder.matches(password, rootUser.getPassword())) {
            throw new NotFoundException(ErrorCode.ROOT_USER_MISMATCH_PASSWORD);
        }
        return rootUser;
    }
}
