package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.application.port.out.dto.QNoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.QNoticeSearchDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import com.kustacks.kuring.user.application.port.out.dto.QBookmarkDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QDepartmentNotice.departmentNotice;
import static com.kustacks.kuring.notice.domain.QNotice.notice;

@RequiredArgsConstructor
class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    @Override
    public List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable) {
        StringTemplate postedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                notice.noticeDateTime.postedDate,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );

        return queryFactory
                .select(
                        new QNoticeDto(
                                notice.articleId,
                                postedDate,
                                notice.url.value,
                                notice.subject,
                                notice.categoryName.stringValue().toLowerCase(),
                                notice.important)
                ).from(notice)
                .where(notice.categoryName.eq(categoryName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.noticeDateTime.postedDate.desc())
                .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoticeSearchDto> findAllByKeywords(List<String> keywords) {
        StringTemplate postedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                notice.noticeDateTime.postedDate,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );

        return queryFactory
                .select(new QNoticeSearchDto(
                        notice.articleId,
                        postedDate,
                        notice.subject,
                        notice.categoryName.stringValue().toLowerCase(),
                        notice.url.value))
                .from(notice)
                .where(isContainSubject(keywords).or(isContainCategory(keywords)))
                .orderBy(notice.noticeDateTime.postedDate.desc())
                .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findNormalArticleIdsByCategory(CategoryName categoryName) {
        return queryFactory
                .select(notice.articleId)
                .from(notice)
                .where(notice.categoryName.eq(categoryName))
                .orderBy(notice.articleId.asc())
                .fetch();
    }

    @Transactional
    @Override
    public void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds) {
        if (articleIds.isEmpty()) {
            return;
        }

        queryFactory
                .delete(notice)
                .where(notice.categoryName.eq(categoryName)
                        .and(notice.articleId.in(articleIds)))
                .execute();
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findImportantArticleIdsByCategoryName(CategoryName categoryName) {
        return queryFactory
                .select(notice.articleId)
                .from(notice)
                .where(notice.categoryName.eq(categoryName)
                        .and(notice.important.eq(true)))
                .orderBy(notice.articleId.asc())
                .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findNormalArticleIdsByCategoryName(CategoryName categoryName) {
        return queryFactory
                .select(notice.articleId)
                .from(notice)
                .where(notice.categoryName.eq(categoryName)
                        .and(notice.important.eq(false)))
                .orderBy(notice.articleId.asc())
                .fetch();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName) {
        StringTemplate postedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                departmentNotice.noticeDateTime.postedDate,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );

        return queryFactory
                .select(new QNoticeDto(
                        departmentNotice.articleId,
                        postedDate,
                        departmentNotice.url.value,
                        departmentNotice.subject,
                        departmentNotice.categoryName.stringValue().toLowerCase(),
                        departmentNotice.important))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.isTrue()))
                .orderBy(departmentNotice.noticeDateTime.postedDate.desc())
                .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable) {
        StringTemplate postedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                departmentNotice.noticeDateTime.postedDate,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );

        return queryFactory
                .select(new QNoticeDto(
                        departmentNotice.articleId,
                        postedDate,
                        departmentNotice.url.value,
                        departmentNotice.subject,
                        departmentNotice.categoryName.stringValue().toLowerCase(),
                        departmentNotice.important))
                .from(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.important.isFalse()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(departmentNotice.noticeDateTime.postedDate.desc())
                .fetch();
    }

    @Transactional
    @Override
    public void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds) {
        if (articleIds.isEmpty()) {
            return;
        }

        queryFactory
                .delete(departmentNotice)
                .where(departmentNotice.departmentName.eq(departmentName)
                        .and(departmentNotice.articleId.in(articleIds)))
                .execute();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookmarkDto> findAllByBookmarkIds(List<String> ids) {
        StringTemplate postedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                notice.noticeDateTime.postedDate,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );

        return queryFactory.select(
                        new QBookmarkDto(
                                notice.articleId,
                                postedDate,
                                notice.subject,
                                notice.categoryName.stringValue(),
                                notice.url.value
                        )
                ).from(notice)
                .where(notice.articleId.in(ids))
                .orderBy(notice.noticeDateTime.postedDate.desc())
                .fetch();
    }

    private static BooleanBuilder isContainSubject(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String containedName : keywords) {
            NumberTemplate<Double> booleanTemplate =
                    Expressions.numberTemplate(
                            Double.class,
                            "function('match',{0},{1})",
                            notice.subject,
                            "*" + containedName + "*"
                    );
            booleanBuilder.or(booleanTemplate.gt(0));
        }

        return booleanBuilder;
    }

    private static BooleanBuilder isContainCategory(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String containedName : keywords) {
            booleanBuilder.or(notice.categoryName.stringValue().toLowerCase().contains(containedName));
        }

        return booleanBuilder;
    }
}
