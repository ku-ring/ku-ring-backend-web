package com.kustacks.kuring.feedback.business;

import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {

    private final UserRepository userRepository;

    public void saveFeedback(String token, String content) {
        Optional<User> optionalUser = userRepository.findByToken(token);
        if(optionalUser.isEmpty()) {
            optionalUser = Optional.of(userRepository.save(new User(token)));
        }
        User findUser = optionalUser.orElseThrow(UserNotFoundException::new);

        findUser.addFeedback(content);
    }
}
