package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.common.utils.generator.RandomGenerator;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDecreaseQuestionCountCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginResult;
import com.kustacks.kuring.user.application.port.in.dto.UserLogoutCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSignupCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSubscribeCompareResult;
import com.kustacks.kuring.user.application.port.out.DeviceQueryPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.Device;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;
import static com.kustacks.kuring.user.domain.User.EMAIL_USER_EXTRA_QUESTION_COUNT;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
class UserCommandService implements UserCommandUseCase {

    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;
    private final DeviceQueryPort deviceQueryPort;
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
            findUser.decreaseQuestionCount();
        } catch (IllegalStateException e) {
            throw new InvalidStateException(ErrorCode.QUESTION_COUNT_NOT_ENOUGH);
        }
    }

    @Override
    public void signupUser(UserSignupCommand userSignupCommand) {
        String nickname = createNickname();
        User emailUser = new User(userSignupCommand.email(),
                passwordEncoder.encode(userSignupCommand.password()),
                nickname);

        checkDuplicateEmailAndSave(emailUser);
    }

    @Transactional
    @Override
    public UserLoginResult login(UserLoginCommand userLoginCommand) {
        Device device = findDeviceByToken(userLoginCommand.fcmToken()); // 어떤 기기에서 로그인할건지를 구분
        User emailUser = findUserByEmailAndPassword(userLoginCommand.email(), userLoginCommand.password()); // 이메일 유저가 있는지 확인하고 가져옴.

        syncQuestionCount(emailUser, device.getUser());
        changeUserOfDevice(emailUser, device);

        String token = jwtTokenProvider.createUserToken(userLoginCommand.email());
        return new UserLoginResult(token);
    }

    @Transactional
    @Override
    public void logout(UserLogoutCommand userLogoutCommand) {
        Device device = findDeviceByToken(userLogoutCommand.fcmToken());

        validateDeviceUser(userLogoutCommand.email(), device.getUser());

        syncQuestionCount(device.getUser(), device.getOriginUser());
        changeUserOfDevice(device.getOriginUser(), device);
    }

    private void changeUserOfDevice(User newUserOfDevice, Device device) {
        device.getUser().logout(device); // 원래 로그인된 사용자를 먼저 해제.
        newUserOfDevice.login(device);
    }

    private void validateDeviceUser(String email, User deviceUser) {
        if (!deviceUser.getEmail().equals(email)) {
            throw new InvalidStateException(ErrorCode.USER_MISMATCH_DEVICE);
        }
    }

    private void syncQuestionCount(User emailUser, User tokenUser) {
        // minQuestionCount = Min(이메일 계정에 남아있는 횟수 - 이메일 계정 추가 횟수, 기기에 남은 횟수)
        int fcmUserQuestionCount = Math.min(emailUser.getQuestionCount() - EMAIL_USER_EXTRA_QUESTION_COUNT, tokenUser.getQuestionCount());
        int emailUserQuestionCount = fcmUserQuestionCount + EMAIL_USER_EXTRA_QUESTION_COUNT;

        if (fcmUserQuestionCount < 0) {
            fcmUserQuestionCount = 0;
        }

        if(emailUserQuestionCount < 0) {
            emailUserQuestionCount = 0;
        }

        tokenUser.updateQuestionCount(fcmUserQuestionCount);
        emailUser.updateQuestionCount(emailUserQuestionCount);
    }

    private void checkDuplicateEmailAndSave(User emailUser) {
        if (userQueryPort.existByEmail(emailUser.getEmail())) {
            throw new InvalidStateException(ErrorCode.EMAIL_DUPLICATE);
        }
        userCommandPort.save(emailUser);
    }

    private String createNickname() {
        String nickname = "";
        do {
            nickname = RandomGenerator.generateRandomNickname(6);
        } while (userQueryPort.existByNickname(nickname));
        return nickname;
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

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userQueryPort.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userCommandPort.save(new User(token)));
            userEventPort.subscribeEvent(token, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
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

    private Device findDeviceByToken(String fcmToken) {
        return deviceQueryPort.findDeviceByToken(fcmToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_DEVICE_NOT_FOUND));
    }

    private User findUserByEmailAndPassword(String email, String password) {
        User user = userQueryPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_SIGNUP));
        if (!passwordEncoder.matches(password,user.getPassword())) {
            throw new NotFoundException(ErrorCode.USER_MISMATCH_PASSWORD);
        }
        return user;
    }
}
