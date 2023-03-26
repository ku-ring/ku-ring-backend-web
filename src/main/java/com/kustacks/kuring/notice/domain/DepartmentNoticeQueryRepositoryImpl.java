package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.worker.DepartmentName;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QDepartmentNotice.departmentNotice;

@RequiredArgsConstructor
public class DepartmentNoticeQueryRepositoryImpl implements DepartmentNoticeQueryRepository {

    private static final int TOTAL_NOTICE_COUNT_PER_PAGE = 12;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findTopTwentyFourArticleIdByDepartment(DepartmentName departmentName) {
        return queryFactory
                .select(departmentNotice.articleId)
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName))
                .limit(TOTAL_NOTICE_COUNT_PER_PAGE * 2)
                .fetch();
    }
}
