package com.kustacks.kuring.notice.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.data.CursorBasedList;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.*;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentReadingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentReadingUseCase.CommentAndSubCommentsResult;
import com.kustacks.kuring.notice.application.port.in.NoticeQueryUseCase;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeContentSearchResult;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeRangeLookupCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Tag(name = "Notice-Query", description = "공지 정보 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/notices")
public class NoticeQueryApiV2 {

    private final NoticeQueryUseCase noticeQueryUseCase;
    private final NoticeCommentReadingUseCase noticeCommentReadingUseCase;

    @Operation(summary = "공지 조회", description = "일반 공지 조회와 학과별 공지 조회를 지원합니다")
    @GetMapping
    public ResponseEntity<BaseResponse<List<NoticeRangeLookupResponse>>> getNotices(
        @Parameter(description = "공지 타입") @RequestParam(name = "type") String type,
        @Parameter(description = "학과는 hostPrefix 로 전달") @RequestParam(name = "department", required = false) String department,
        @Parameter(description = "중요도") @RequestParam(name = "important", defaultValue = "false") Boolean important,
        @Parameter(description = "페이지") @RequestParam(name = "page") @Min(0) int page,
        @Parameter(description = "단일 페이지의 사이즈, 1 ~ 30까지 허용") @RequestParam(name = "size") @Min(1) @Max(30) int size
    ) {
        NoticeRangeLookupCommand command = new NoticeRangeLookupCommand(type, department, important, page, size);
        List<NoticeRangeLookupResponse> searchResults = noticeQueryUseCase.getNotices(command)
                .stream()
                .map(NoticeRangeLookupResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, searchResults));
    }

    @Operation(summary = "키워드 공지 조회", description = "일반 공지 조회와 학과별 공지 검색을 지원하며, 2글자 이상의 키워드를 입력하길 권장합니다")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<NoticeContentSearchResponse>> searchNotice(
            @NotBlank @RequestParam(name = "content") String content
    ) {
        List<NoticeContentSearchResult> response = noticeQueryUseCase.findAllNoticeByContent(content);
        NoticeContentSearchResponse noticeContentSearchResponse = new NoticeContentSearchResponse(response);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, noticeContentSearchResponse));
    }

    @Operation(summary = "일반 공지 카테고리", description = "서버가 지원하는 일반공지 카테고리 목록을 조회합니다")
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<NoticeCategoryNameResponse>>> getSupportedCategories() {
        List<NoticeCategoryNameResponse> categoryNames = noticeQueryUseCase.lookupSupportedCategories()
            .stream()
            .map(NoticeCategoryNameResponse::from)
            .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SEARCH_SUCCESS, categoryNames));
    }

    @Operation(summary = "학과별 공지 카테고리", description = "서버가 지원하는 학과별 공지 카테고리 목록을 조회합니다")
    @GetMapping("/departments")
    public ResponseEntity<BaseResponse<List<NoticeDepartmentNameResponse>>> getSupportedDepartments() {
        List<NoticeDepartmentNameResponse> departmentNames = noticeQueryUseCase.lookupSupportedDepartments()
            .stream()
            .map(NoticeDepartmentNameResponse::from)
            .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SEARCH_SUCCESS, departmentNames));
    }

    @Operation(summary = "댓글 조회", description = "특정 공지에 추가된 모든 댓글을 조회합니다")
    @GetMapping("/{id}/comments")
    public ResponseEntity<BaseResponse<CommentListResponse>> getComment(
            @Parameter(description = "공지 ID") @PathVariable(name = "id") Long id,
            @Parameter(description = "커서") @RequestParam(name = "cursor", required = false) String cursor,
            @Parameter(description = "단일 요청의 사이즈, 1 ~ 30까지 허용")
            @RequestParam(name = "size", required = true, defaultValue = "10") @Min(1) @Max(30) Integer size
    ) {
        CursorBasedList<CommentAndSubCommentsResult> comments = noticeCommentReadingUseCase.findComments(id, cursor, size);

        CommentListResponse response = new CommentListResponse(
                comments,
                comments.getEndCursor(),
                comments.hasNext()
        );

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, response));
    }
}
