package com.kustacks.kuring.user.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.user.domain.QRootUser.rootUser;

@RequiredArgsConstructor
class RootUserQueryRepositoryImpl implements RootUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findExistNicknamesIn(List<String> candidateNicknames) {
        return queryFactory.select(rootUser.nickname)
                .from(rootUser)
                .where(rootUser.nickname.in(candidateNicknames))
                .fetch();
    }
}
