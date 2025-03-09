package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NoPermissionException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentDeletingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentEditingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentWritingUseCase;
import com.kustacks.kuring.notice.application.port.out.CommentCommandPort;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.Comment;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService implements
        NoticeCommentWritingUseCase,
        NoticeCommentEditingUseCase,
        NoticeCommentDeletingUseCase {
    private final NoticeQueryPort noticeQueryPort;
    private final CommentCommandPort commentCommandPort;
    private final CommentQueryPort commentQueryPort;
    private final RootUserQueryPort rootUserQueryPort;

    @Override
    public void process(WriteCommentCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());

        NoticeDto findNotice = findNoticeById(command.noticeId());

        commentCommandPort.createComment(rootUser.getId(), findNotice.getId(), command.content());

        log.info("write notice-comment, user{}, notice{}", rootUser.getId(), findNotice.getId());
    }

    @Override
    public void process(WriteReplyCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());

        NoticeDto findNotice = findNoticeById(command.noticeId());

        CommentReadModel commentReadModel = commentQueryPort.findComment(command.parentId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentReadModel.getDestroyedAt() != null) {
            throw new NotFoundException(ErrorCode.COMMENT_NOT_FOUND);
        }

        commentCommandPort.createReply(rootUser.getId(), findNotice.getId(), command.parentId(), command.content());

        log.info("reply notice-comment, user{}, notice{}", rootUser.getId(), findNotice.getId());
    }

    @Override
    public void process(EditCommentCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());

        NoticeDto findNotice = findNoticeById(command.noticeId());

        Comment findComment = findCommentById(command.commentId());

        if (isNotCommentOwner(findComment, rootUser, findNotice)) {
            throw new NoPermissionException(ErrorCode.COMMENT_NOT_FOUND);
        }

        findComment.editContent(command.content());

        log.info("edit notice-comment, user{}, notice{}, comment{}", rootUser.getId(), findNotice.getId(), findComment.getId());
    }

    @Override
    public void process(DeleteCommentCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());

        NoticeDto findNotice = findNoticeById(command.noticeId());

        Comment findComment = findCommentById(command.commentId());

        if (isNotCommentOwner(findComment, rootUser, findNotice)) {
            throw new NoPermissionException(ErrorCode.COMMENT_NOT_FOUND);
        }

        commentCommandPort.delete(findComment);

        log.info("delete notice-comment, user{}, notice{}, comment{}", rootUser.getId(), findNotice.getId(), findComment.getId());
    }

    private static boolean isNotCommentOwner(Comment findComment, RootUser findUser, NoticeDto findNotice) {
        return !Objects.equals(findComment.getRootUserId(), findUser.getId())
                || !Objects.equals(findComment.getNoticeId(), findNotice.getId())
                || findComment.getDestroyedAt() != null;
    }

    private RootUser findRootUserByEmail(String email) {
        return rootUserQueryPort.findRootUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOT_USER_NOT_FOUND));
    }

    private NoticeDto findNoticeById(Long noticeId) {
        return noticeQueryPort.findNoticeById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentQueryPort.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
