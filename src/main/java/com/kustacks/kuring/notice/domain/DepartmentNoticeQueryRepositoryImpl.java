package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.QNoticeDto;
import com.kustacks.kuring.worker.DepartmentName;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QDepartmentNotice.departmentNotice;

@RequiredArgsConstructor
public class DepartmentNoticeQueryRepositoryImpl implements DepartmentNoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentName) {
        return queryFactory
                .select(departmentNotice.articleId.castToNum(Integer.class))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.eq(true)))
                .orderBy(departmentNotice.articleId.castToNum(Integer.class).asc())
                .fetch();
    }

    @Override
    public List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentName) {
        return queryFactory
                .select(departmentNotice.articleId.castToNum(Integer.class))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.eq(false)))
                .orderBy(departmentNotice.articleId.castToNum(Integer.class).asc())
                .fetch();
    }

    @Override
    public List<Integer> findNormalArticleIdsByDepartmentWithLimit(DepartmentName departmentName, int limit) {
        return queryFactory
                .select(departmentNotice.articleId.castToNum(Integer.class))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.eq(false)))
                .orderBy(departmentNotice.articleId.castToNum(Integer.class).asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName) {
        return queryFactory
                .select(new QNoticeDto(
                        departmentNotice.articleId,
                        departmentNotice.postedDate,
                        departmentNotice.url.value,
                        departmentNotice.subject,
                        departmentNotice.category.name,
                        departmentNotice.important))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.isTrue()))
                .orderBy(departmentNotice.postedDate.desc())
                .fetch();
    }

    @Override
    public List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable) {
        return queryFactory
                .select(new QNoticeDto(
                        departmentNotice.articleId,
                        departmentNotice.postedDate,
                        departmentNotice.url.value,
                        departmentNotice.subject,
                        departmentNotice.category.name,
                        departmentNotice.important))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.isFalse()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(departmentNotice.postedDate.desc())
                .fetch();
    }

    @Override
    public void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }

        queryFactory
                .delete(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                                .and(departmentNotice.articleId.in(articleIds)))
                .execute();
    }
}
