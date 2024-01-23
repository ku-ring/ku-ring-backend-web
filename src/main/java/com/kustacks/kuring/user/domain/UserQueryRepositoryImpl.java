package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.admin.common.dto.QFeedbackDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.kustacks.kuring.user.domain.QFeedback.feedback;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable) {
        return queryFactory.select(new QFeedbackDto(feedback.content.value, feedback.user.id, feedback.createdAt))
                .from(feedback)
                .orderBy(feedback.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
