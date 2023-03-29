package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.QNoticeDto;
import com.kustacks.kuring.search.common.dto.NoticeSearchDto;
import com.kustacks.kuring.search.common.dto.QNoticeSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QNotice.notice;

@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;


    @Override
    public List<NoticeDto> findNoticesByCategoryWithOffset(Category category, Pageable pageable) {
        return queryFactory
                .select(new QNoticeDto(notice.articleId, notice.postedDate, notice.url.value, notice.subject, notice.category.name, notice.important))
                .from(notice)
                .where(notice.category.eq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.postedDate.desc())
                .fetch();
    }

    @Override
    public List<NoticeSearchDto> findAllByKeywords(List<String> keywords) {
        List<NoticeSearchDto> searchDtoList = queryFactory
                .select(new QNoticeSearchDto(
                        notice.articleId,
                        notice.postedDate,
                        notice.subject,
                        notice.category.name))
                .from(notice)
                .where(isContainSubject(keywords).or(isContainCategory(keywords)))
                .orderBy(notice.postedDate.desc())
                .fetch();

        searchDtoList
                .forEach(dto -> dto.setBaseUrl(
                        dto.getCategoryName().equals("library") ? libraryBaseUrl : normalBaseUrl)
                );

        return searchDtoList;
    }

    private static BooleanBuilder isContainSubject(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String containedName : keywords) {
            booleanBuilder.or(notice.subject.contains(containedName));
        }

        return booleanBuilder;
    }

    private static BooleanBuilder isContainCategory(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String containedName : keywords) {
            booleanBuilder.or(notice.category.name.contains(containedName));
        }

        return booleanBuilder;
    }
}
