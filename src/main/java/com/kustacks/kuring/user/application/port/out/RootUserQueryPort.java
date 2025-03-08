package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.RootUser;

import java.util.Optional;

public interface RootUserQueryPort {
    Optional<RootUser> findRootUserByEmail(String email);

    boolean existRootUserByEmail(String email);
}
