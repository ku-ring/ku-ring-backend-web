package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.User;

public interface UserCommandPort {
    User save(User user);
    void delete(User user);
}
