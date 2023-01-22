package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.notice.common.dto.response.NoticeDto;
import com.kustacks.kuring.notice.common.dto.response.QNoticeDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kustacks.kuring.notice.domain.QNotice.notice;

@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NoticeDto> findNoticesByCategoryWithOffset(Category category, Pageable pageable) {
        return queryFactory
                .select(new QNoticeDto(notice.articleId, notice.postedDate, notice.subject, notice.category.name))
                .from(notice)
                .where(notice.category.eq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notice.postedDate.desc())
                .fetch();
    }
}
