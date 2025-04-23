package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.user.domain.RootUser;

import java.util.List;
import java.util.Optional;

public interface RootUserQueryPort {
    Optional<RootUser> findRootUserByEmail(String email);
    Optional<RootUser> findDeletedRootUserByEmail(String email);

    boolean existRootUserByEmail(String email);

    List<String> findUsingNicknamesIn(List<String> candidateNicknames);
}
