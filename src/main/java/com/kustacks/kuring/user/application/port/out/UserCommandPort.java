package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.User;

import java.util.List;

public interface UserCommandPort {
    User save(User user);
    void deleteAll(List<User> allInvalidUsers);
    void resetAllUserQuestionCount();
}
