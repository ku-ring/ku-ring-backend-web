package com.kustacks.kuring.user.business;

import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(UserNotFoundException::new);
    }
}
