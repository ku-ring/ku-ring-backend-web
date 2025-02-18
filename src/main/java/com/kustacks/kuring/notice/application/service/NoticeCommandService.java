package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.in.NoticeCommandUseCase;
import com.kustacks.kuring.notice.application.port.out.CommentCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class NoticeCommandService implements NoticeCommandUseCase {

    private final NoticeQueryPort noticeQueryPort;
    private final CommentCommandPort commentCommandPort;
    private final UserQueryPort userQueryPort;

    @Override
    public void createNotice(NoticeCommentCreateCommand command) {
        User findUser = userQueryPort.findByToken(command.userToken())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        NoticeDto findNotice = noticeQueryPort.findNoticeById(command.noticeId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        commentCommandPort.createComment(findUser.getId(), findNotice.getId(), command.content());
    }
}
