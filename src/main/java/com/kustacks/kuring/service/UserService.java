package com.kustacks.kuring.service;

import com.kustacks.kuring.user.domain.User;

public interface UserService {
    User getUserByToken(String token);
    User insertUserToken(String token);
}
