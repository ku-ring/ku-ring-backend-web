package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.admin.application.port.out.AdminUserFeedbackPort;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.user.application.port.out.RootUserCommandPort;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.user.domain.RootUser.ROOT_USER_MONTHLY_QUESTION_COUNT;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserCommandPort, UserQueryPort, AdminUserFeedbackPort, RootUserCommandPort, RootUserQueryPort {

    private final UserRepository userRepository;
    private final RootUserRepository rootUserRepository;

    @Override
    public List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable) {
        return userRepository.findAllFeedbackByPageRequest(pageable);
    }

    @Override
    public List<String> findAllToken() {
        return userRepository.findAllFcmTokens();
    }

    @Override
    public Optional<User> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userRepository.findByFcmToken(token);
    }

    @Override
    public Optional<RootUser> findRootUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return rootUserRepository.findByEmail(email);
    }

    @Override
    public Optional<RootUser> findDeletedRootUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return rootUserRepository.findDeletedRootUserByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByPageRequest(Pageable pageable) {
        return userRepository.findByPageRequest(pageable);
    }

    @Override
    public Long countUser() {
        return this.userRepository.count();
    }

    @Override
    public RootUser saveRootUser(RootUser rootUser) {
        return rootUserRepository.save(rootUser);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existByNickname(String nickname) {
        return rootUserRepository.findByNickname(nickname).isPresent();
    }

    @Override
    public List<String> findUsingNicknamesIn(List<String> candidateNicknames) {
        return rootUserRepository.findExistNicknamesIn(candidateNicknames);
    }

    @Override
    public List<User> findByLoggedInUserId(Long id) {
        return userRepository.findByLoginUserId(id);
    }

    @Override
    public boolean existRootUserByEmail(String email) {
        return rootUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public void deleteAll(List<User> allInvalidUsers) {
        userRepository.deleteAll(allInvalidUsers);
    }

    @Override
    public void resetAllUserQuestionCount() {
        userRepository.resetAllUserQuestionCount();
    }

    @Override
    public void resetAllRootUserQuestionCount() {
        rootUserRepository.resetAllRootUserQuestionCount(ROOT_USER_MONTHLY_QUESTION_COUNT);
    }

    @Override
    public void deleteRootUser(RootUser rootUser) {
        rootUserRepository.delete(rootUser);
    }
}
