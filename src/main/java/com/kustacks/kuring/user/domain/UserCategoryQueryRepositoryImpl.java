package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.category.domain.CategoryName;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.category.domain.QCategory.category;
import static com.kustacks.kuring.user.domain.QUserCategory.userCategory;

@RequiredArgsConstructor
public class UserCategoryQueryRepositoryImpl implements UserCategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryName> getUserCategoryNamesByToken(String token) {
        return queryFactory
                .select(userCategory.category.categoryName)
                .from(userCategory)
                .where(userCategory.user.token.eq(token))
                .fetch();
    }

    @Override
    public List<UserCategory> getUserCategoriesByToken(String token) {
        return queryFactory
                .selectFrom(userCategory)
                .join(userCategory.category, category).fetchJoin()
                .where(userCategory.user.token.eq(token))
                .fetch();
    }
}
