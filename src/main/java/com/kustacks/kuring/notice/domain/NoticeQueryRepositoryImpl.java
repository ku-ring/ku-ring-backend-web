package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.common.dto.QNoticeDto;
import com.kustacks.kuring.notice.common.dto.QNoticeSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QNotice.notice;

@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable) {
        return queryFactory
                .select(new QNoticeDto(notice.articleId, notice.postedDate, notice.url.value, notice.subject, notice.categoryName.stringValue().toLowerCase(), notice.important))
                .from(notice)
                .where(notice.categoryName.eq(categoryName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.postedDate.desc())
                .fetch();
    }

    @Override
    public List<NoticeSearchDto> findAllByKeywords(List<String> keywords) {
        return queryFactory
                .select(new QNoticeSearchDto(
                        notice.articleId,
                        notice.postedDate,
                        notice.subject,
                        notice.categoryName.stringValue().toLowerCase(),
                        notice.url.value))
                .from(notice)
                .where(isContainSubject(keywords).or(isContainCategory(keywords)))
                .orderBy(notice.postedDate.desc())
                .fetch();
    }

    @Override
    public List<String> findNormalArticleIdsByCategory(CategoryName categoryName) {
        return queryFactory
                .select(notice.articleId)
                .from(notice)
                .where(notice.categoryName.eq(categoryName))
                .orderBy(notice.articleId.asc())
                .fetch();
    }

    @Override
    public void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }

        queryFactory
                .delete(notice)
                .where(notice.categoryName.eq(categoryName)
                        .and(notice.articleId.in(articleIds)))
                .execute();
    }

    private static BooleanBuilder isContainSubject(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String containedName : keywords) {
            NumberTemplate<Double> booleanTemplate = Expressions.numberTemplate(Double.class, "function('match',{0},{1})", notice.subject, "*" + containedName + "*");
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
