package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentQueryPort {

    Optional<CommentReadModel> findComment(Long id);

    List<CommentReadModel> findExcludeSubCommentByCursor(Long noticeId, String cursor, int size);

    List<CommentReadModel> findSubCommentByIds(Set<Long> parentCommentIds);
}
