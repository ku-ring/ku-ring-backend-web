package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.BadWordContainsException;
import com.kustacks.kuring.common.exception.NoPermissionException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.in.BadWordInitProcessor;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentDeletingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentEditingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentWritingUseCase;
import com.kustacks.kuring.notice.application.port.out.BadWordsQueryPort;
import com.kustacks.kuring.notice.application.port.out.CommentCommandPort;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.BadWord;
import com.kustacks.kuring.notice.domain.Comment;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService implements
        NoticeCommentWritingUseCase,
        NoticeCommentEditingUseCase,
        NoticeCommentDeletingUseCase,
        BadWordInitProcessor {
    private static final String COMMENT_REGEX = "[^가-힣a-zA-Z0-9]";
    private final NoticeQueryPort noticeQueryPort;
    private final CommentCommandPort commentCommandPort;
    private final CommentQueryPort commentQueryPort;
    private final RootUserQueryPort rootUserQueryPort;
    private final BadWordsQueryPort badWordQueryPort;

    private Trie badWordTrie;

    @PostConstruct
    public void badWordInit() {
        process();
    }

    @Override
    public void process(WriteCommentCommand command) {
        validateText(command.content());

        RootUser rootUser = findRootUserByEmailOrThrow(command.email());

        NoticeDto findNotice = findNoticeByIdOrThrow(command.noticeId());

        commentCommandPort.createComment(rootUser.getId(), findNotice.getId(), command.content());

        log.info("write notice-comment, user{}, notice{}", rootUser.getId(), findNotice.getId());
    }

    @Override
    public void process(WriteReplyCommand command) {
        validateText(command.content());

        RootUser rootUser = findRootUserByEmailOrThrow(command.email());

        NoticeDto findNotice = findNoticeByIdOrThrow(command.noticeId());

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
        validateText(command.content());

        RootUser rootUser = findRootUserByEmailOrThrow(command.email());

        NoticeDto findNotice = findNoticeByIdOrThrow(command.noticeId());

        Comment findComment = findCommentByIdOrThrow(command.commentId());

        if (isNotCommentOwner(findComment, rootUser, findNotice)) {
            throw new NoPermissionException(ErrorCode.COMMENT_NOT_FOUND);
        }

        findComment.editContent(command.content());

        log.info("edit notice-comment, user{}, notice{}, comment{}", rootUser.getId(), findNotice.getId(), findComment.getId());
    }

    @Override
    public void process(DeleteCommentCommand command) {
        RootUser rootUser = findRootUserByEmailOrThrow(command.email());

        NoticeDto findNotice = findNoticeByIdOrThrow(command.noticeId());

        Comment findComment = findCommentByIdOrThrow(command.commentId());

        if (isNotCommentOwner(findComment, rootUser, findNotice)) {
            throw new NoPermissionException(ErrorCode.COMMENT_NOT_FOUND);
        }

        commentCommandPort.delete(findComment);

        log.info("delete notice-comment, user{}, notice{}, comment{}", rootUser.getId(), findNotice.getId(), findComment.getId());
    }

    @Override
    public void process() {
        List<BadWord> activeBadWords = badWordQueryPort.findAllByActive();
        if (!activeBadWords.isEmpty()) {
            Trie.TrieBuilder builder = Trie.builder().ignoreCase();

            for (BadWord badWord : activeBadWords) {
                builder.addKeyword(badWord.getWord());
            }

            badWordTrie = builder.build();
        }

        log.info("금칙어 로드 완료 - 총 {} 개", activeBadWords.size());
    }

    private void validateText(String content) {
        if (badWordTrie != null) {
            // 특수문자 제거 및 소문자 변환
            String normalizedContent = content.replaceAll(COMMENT_REGEX, "").toLowerCase();

            // 전체 텍스트에 대해 금칙어 검사
            Collection<Emit> matches = badWordTrie.parseText(normalizedContent);
            if (!matches.isEmpty()) {
                throw new BadWordContainsException(ErrorCode.COMMENT_BAD_WORD_CONTAINS);
            }
        }
    }

    private static boolean isNotCommentOwner(Comment findComment, RootUser findUser, NoticeDto findNotice) {
        return !Objects.equals(findComment.getRootUserId(), findUser.getId())
                || !Objects.equals(findComment.getNoticeId(), findNotice.getId())
                || findComment.getDestroyedAt() != null;
    }

    private RootUser findRootUserByEmailOrThrow(String email) {
        return rootUserQueryPort.findRootUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOT_USER_NOT_FOUND));
    }

    private NoticeDto findNoticeByIdOrThrow(Long noticeId) {
        return noticeQueryPort.findNoticeById(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private Comment findCommentByIdOrThrow(Long commentId) {
        return commentQueryPort.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
