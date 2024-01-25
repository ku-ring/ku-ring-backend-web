package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserCommandPort, UserQueryPort {

    private final UserRepository userRepository;

    @Override
    public List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable) {
        return userRepository.findAllFeedbackByPageRequest(pageable);
    }

    @Override
    public List<String> findAllToken() {
        return userRepository.findAllToken();
    }

    @Override
    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
