package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentQueryRepository {

    Optional<CommentReadModel> findReadModelById(Long id);

    List<CommentReadModel> findExcludeSubCommentByCursor(Long noticeId, String cursor, int size);

    List<CommentReadModel> findSubCommentByIds(Long noticeId, Set<Long> parentCommentIds);
}
