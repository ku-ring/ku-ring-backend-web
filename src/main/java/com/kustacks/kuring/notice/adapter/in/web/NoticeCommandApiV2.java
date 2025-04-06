package com.kustacks.kuring.notice.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeCommentCreateRequest;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeCommentEditRequest;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentDeletingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentDeletingUseCase.DeleteCommentCommand;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentEditingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentEditingUseCase.EditCommentCommand;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentWritingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentWritingUseCase.WriteCommentCommand;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentWritingUseCase.WriteReplyCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Tag(name = "Notice-Command", description = "공지 가공")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/notices")
public class NoticeCommandApiV2 {

    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final NoticeCommentWritingUseCase noticeCommentWritingUseCase;
    private final NoticeCommentEditingUseCase noticeCommentEditingUseCase;
    private final NoticeCommentDeletingUseCase noticeCommentDeletingUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "공지 댓글 추가", description = "공지에 댓글을 추가합니다")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PostMapping("/{id}/comments")
    public ResponseEntity<BaseResponse> createComment(
            @Parameter(description = "공지 ID") @PathVariable("id") Long id,
            @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken,
            @RequestBody NoticeCommentCreateRequest request
    ) {
        String jwtToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        if (request.parentId() == null) {
            var command = new WriteCommentCommand(
                    email,
                    id,
                    request.content()
            );

            noticeCommentWritingUseCase.process(command);
        } else {
            var command = new WriteReplyCommand(
                    email,
                    id,
                    request.content(),
                    request.parentId()
            );

            noticeCommentWritingUseCase.process(command);
        }

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_COMMENT_SAVE_SUCCESS, null));
    }

    @Operation(summary = "공지 댓글 편집", description = "공지에 있는 기존의 댓글을 수정합니다")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PostMapping("/{id}/comments/{commentId}")
    public ResponseEntity<BaseResponse> editComment(
            @Parameter(description = "공지 ID") @PathVariable("id") Long id,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken,
            @RequestBody NoticeCommentEditRequest request
    ) {
        String jwtToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        var command = new EditCommentCommand(email, id, commentId, request.content());

        noticeCommentEditingUseCase.process(command);

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_COMMENT_EDIT_SUCCESS, null));
    }

    @Operation(summary = "공지 댓글 삭제", description = "공지에 있는 기존의 댓글을 삭제합니다")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity deleteComment(
            @Parameter(description = "공지 ID") @PathVariable("id") Long id,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String jwtToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        var command = new DeleteCommentCommand(email, id, commentId);

        noticeCommentDeletingUseCase.process(command);

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_COMMENT_DELETE_SUCCESS, null));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }
}
