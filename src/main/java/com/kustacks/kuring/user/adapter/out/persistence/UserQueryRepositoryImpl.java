package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.user.application.port.out.dto.QFeedbackDto;
import com.kustacks.kuring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustacks.kuring.user.domain.QFeedback.feedback;
import static com.kustacks.kuring.user.domain.QUser.user;
import static com.kustacks.kuring.user.domain.User.FCM_USER_MONTHLY_QUESTION_COUNT;

@RequiredArgsConstructor
class UserQueryRepositoryImpl implements UserQueryRepository {

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

    @Override
    public List<User> findByPageRequest(Pageable pageable) {
        return queryFactory
                .selectFrom(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Transactional
    @Override
    public void resetAllUserQuestionCount() {
        queryFactory.update(user)
                .set(user.questionCount, FCM_USER_MONTHLY_QUESTION_COUNT)
                .execute();
    }
}
