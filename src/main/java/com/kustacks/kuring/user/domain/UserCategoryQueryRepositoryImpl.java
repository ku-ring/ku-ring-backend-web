package com.kustacks.kuring.user.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.user.domain.QUserCategory.userCategory;

@RequiredArgsConstructor
public class UserCategoryQueryRepositoryImpl implements UserCategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> getUserCategoryNamesByToken(String token) {
        return queryFactory
                .select(userCategory.category.name)
                .from(userCategory)
                .where(userCategory.user.token.eq(token))
                .fetch();
    }
}