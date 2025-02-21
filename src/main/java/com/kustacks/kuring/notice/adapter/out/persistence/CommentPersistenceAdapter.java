package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.CommentCommandPort;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;
import com.kustacks.kuring.notice.domain.Comment;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@PersistenceAdapter
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements CommentCommandPort, CommentQueryPort {

    private final CommentRepository commentRepository;

    @Override
    public void createComment(Long userId, Long noticeId, String content) {
        Comment comment = new Comment(userId, noticeId, content);
        this.commentRepository.save(comment);
    }

    @Override
    public void createReply(Long userId, Long noticeId, Long parentId, String content) {
        Comment comment = new Comment(userId, noticeId, parentId, content);
        this.commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public Optional<CommentReadModel> findComment(Long id) {
        return commentRepository.findReadModelById(id);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<CommentReadModel> findExcludeSubCommentByCursor(Long noticeId, String cursor, int size) {
        return commentRepository.findExcludeSubCommentByCursor(noticeId, cursor, size);
    }

    @Override
    public List<CommentReadModel> findSubCommentByIds(Long noticeId, Set<Long> parentCommentIds) {
        return commentRepository.findSubCommentByIds(noticeId, parentCommentIds);
    }
}
