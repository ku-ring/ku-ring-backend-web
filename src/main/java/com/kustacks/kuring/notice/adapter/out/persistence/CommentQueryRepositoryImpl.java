package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;
import com.kustacks.kuring.notice.application.port.out.dto.QCommentReadModel;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kustacks.kuring.notice.domain.QComment.comment;

@RequiredArgsConstructor
class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentReadModel> findReadModelById(Long id) {
        CommentReadModel commentReadModel = queryFactory.select(
                        new QCommentReadModel(
                                comment.id,
                                comment.parentId,
                                comment.userId,
                                comment.noticeId,
                                comment.content.value,
                                comment.destroyedAt,
                                comment.createdAt,
                                comment.updatedAt
                        )
                ).from(comment)
                .where(comment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(commentReadModel);
    }

    @Override
    public List<CommentReadModel> findExcludeSubCommentByCursor(Long noticeId, String cursor, int size) {
        return queryFactory.select(
                        new QCommentReadModel(
                                comment.id,
                                comment.parentId,
                                comment.userId,
                                comment.noticeId,
                                comment.content.value,
                                comment.destroyedAt,
                                comment.createdAt,
                                comment.updatedAt
                        )
                ).from(comment)
                .where(comment.noticeId.eq(noticeId), comment.parentId.isNull(), cursorId(cursor))
                .orderBy(comment.id.asc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<CommentReadModel> findSubCommentByIds(Set<Long> parentCommentIds) {
        return queryFactory.select(
                        new QCommentReadModel(
                                comment.id,
                                comment.parentId,
                                comment.userId,
                                comment.noticeId,
                                comment.content.value,
                                comment.destroyedAt,
                                comment.createdAt,
                                comment.updatedAt
                        )
                ).from(comment)
                .where(comment.parentId.in(parentCommentIds))
                .fetch();
    }

    private static BooleanExpression cursorId(String cursorId) {
        return cursorId == null ? null : comment.id.goe(Long.parseLong(cursorId));
    }
}
