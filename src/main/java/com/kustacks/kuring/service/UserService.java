package com.kustacks.kuring.service;

import com.kustacks.kuring.persistence.user.User;

public interface UserService {
    User getUserByToken(String token);
    User insertUserToken(String token);
}
