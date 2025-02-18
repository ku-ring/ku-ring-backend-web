package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.CommentCommandPort;
import com.kustacks.kuring.notice.domain.Comment;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements CommentCommandPort {

    private final CommentRepository commentRepository;

    @Override
    public void createComment(Long userId, Long noticeId, String content) {
        Comment comment = new Comment(userId, noticeId, content);
        this.commentRepository.save(comment);
    }
}
