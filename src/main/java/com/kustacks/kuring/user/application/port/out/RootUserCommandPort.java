package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.RootUser;

public interface RootUserCommandPort {
    RootUser saveRootUser(RootUser rootUser);

    void resetAllRootUserQuestionCount();
}
