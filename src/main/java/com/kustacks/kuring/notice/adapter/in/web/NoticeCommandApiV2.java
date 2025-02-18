package com.kustacks.kuring.notice.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeCommentCreateRequest;
import com.kustacks.kuring.notice.application.port.in.NoticeCommandUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommandUseCase.NoticeCommentCreateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_COMMENT_SAVE_SUCCESS;

@Tag(name = "Notice-Command", description = "공지 가공")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/notices")
public class NoticeCommandApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final NoticeCommandUseCase noticeCommandUseCase;

    @Operation(summary = "공지 댓글 추가", description = "공지에 댓글을 추가합니다")
    @SecurityRequirement(name = USER_TOKEN_HEADER_KEY)
    @PostMapping("/{id}/comments")
    public ResponseEntity<BaseResponse> getSupportedCategories(
            @PathVariable("id") Long id,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken,
            @RequestBody NoticeCommentCreateRequest request
    ) {
        NoticeCommentCreateCommand command = new NoticeCommentCreateCommand(userToken, id, request.content());

        noticeCommandUseCase.createNotice(command);

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_COMMENT_SAVE_SUCCESS, null));
    }
}
