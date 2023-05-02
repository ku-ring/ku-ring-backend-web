package com.kustacks.kuring.category.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.category.domain.QCategory.category;

@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryName> getSupportedCategoryNames() {
        return queryFactory
                .select(category.categoryName)
                .from(category)
                .fetch();
    }
}
