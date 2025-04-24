package com.kustacks.kuring.user.adapter.out.persistence;

import java.util.List;

public interface RootUserQueryRepository {
    List<String> findExistNicknamesIn(List<String> candidateNicknames);
}
