package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.admin.application.port.out.AdminUserFeedbackPort;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.user.application.port.out.DeviceQueryPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.user.domain.Device;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserCommandPort, UserQueryPort, AdminUserFeedbackPort, DeviceQueryPort {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<Device> findDeviceByToken(String fcmToken) {
        return deviceRepository.findByFcmToken(fcmToken);
    }

    @Override
    public List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable) {
        return userRepository.findAllFeedbackByPageRequest(pageable);
    }

    @Override
    public List<String> findAllToken() {
        return deviceRepository.findAllFcmTokens();
    }

    @Override
    public Optional<User> findByToken(String token) {
        return deviceRepository.findUserByFcmToken(token);
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
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existByNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public void deleteAll(List<User> allInvalidUsers) {
        userRepository.deleteAll(allInvalidUsers);
    }

    @Override
    public void resetAllUserQuestionCount() {
        userRepository.resetAllUserQuestionCount();
    }
}
