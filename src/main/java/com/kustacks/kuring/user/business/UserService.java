package com.kustacks.kuring.user.business;

import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    public User insertUserToken(String token) {
        return userRepository.save(User.builder()
                .token(token)
                .build());
    }
}
