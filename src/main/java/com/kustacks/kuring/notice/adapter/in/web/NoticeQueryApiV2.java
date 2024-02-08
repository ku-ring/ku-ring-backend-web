package com.kustacks.kuring.notice.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeCategoryNameResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeContentSearchResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeDepartmentNameResponse;
import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeRangeLookupResponse;
import com.kustacks.kuring.notice.application.port.in.NoticeQueryUseCase;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeContentSearchResult;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeRangeLookupCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/notices")
public class NoticeQueryApiV2 {

    private final NoticeQueryUseCase noticeQueryUseCase;

    @GetMapping
    public ResponseEntity<BaseResponse<List<NoticeRangeLookupResponse>>> getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "important", required = false) Boolean important,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size
    ) {
        NoticeRangeLookupCommand command = new NoticeRangeLookupCommand(type, department, important, page, size);
        List<NoticeRangeLookupResponse> searchResults = noticeQueryUseCase.getNotices(command)
                .stream()
                .map(NoticeRangeLookupResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, searchResults));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<NoticeContentSearchResponse>> searchNotice(
            @NotBlank @RequestParam String content
    ) {
        List<NoticeContentSearchResult> response = noticeQueryUseCase.findAllNoticeByContent(content);
        NoticeContentSearchResponse noticeContentSearchResponse = new NoticeContentSearchResponse(response);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, noticeContentSearchResponse));
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<NoticeCategoryNameResponse>>> getSupportedCategories() {
        List<NoticeCategoryNameResponse> categoryNames = noticeQueryUseCase.lookupSupportedCategories()
                .stream()
                .map(NoticeCategoryNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SEARCH_SUCCESS, categoryNames));
    }

    @GetMapping("/departments")
    public ResponseEntity<BaseResponse<List<NoticeDepartmentNameResponse>>> getSupportedDepartments() {
        List<NoticeDepartmentNameResponse> departmentNames = noticeQueryUseCase.lookupSupportedDepartments()
                .stream()
                .map(NoticeDepartmentNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SEARCH_SUCCESS, departmentNames));
    }
}
