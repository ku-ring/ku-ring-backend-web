package com.kustacks.kuring.service;

import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.persistence.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public User insertUserToken(String token) {
        return userRepository.save(User.builder()
                .token(token)
                .build());
    }

    @Override
    public User enrollUserToken(String token) {
        User user = userRepository.findByToken(token);
        if(user == null) {
            user = userRepository.save(User.builder()
                    .token(token)
                    .build());
        } else {
            user = null;
        }
        return user;
    }
}
