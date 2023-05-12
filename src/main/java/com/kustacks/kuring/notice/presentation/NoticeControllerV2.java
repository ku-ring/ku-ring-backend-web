package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.CategoryNameDto;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeLookupResponse;
import com.kustacks.kuring.notice.common.dto.NoticeSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_SEARCH_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeControllerV2 {

    private final NoticeService noticeService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<NoticeDto>>> getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "important", required = false) Boolean important,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size) {
        List<NoticeDto> searchResults = noticeService.getNoticesV2(type, department, important, page, size);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, searchResults));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<NoticeLookupResponse>> searchNotice(@NotBlank @RequestParam String content) {
        List<NoticeSearchDto> noticeDtoList = noticeService.findAllNoticeByContent(content);
        NoticeLookupResponse response = new NoticeLookupResponse(noticeDtoList);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, response));
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<CategoryNameDto>>> getSupportedCategories() {
        List<CategoryNameDto> categoryNames = categoryService.lookUpSupportedCategories()
                .stream()
                .map(CategoryNameDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SEARCH_SUCCESS, categoryNames));
    }

    @GetMapping("/departments")
    public ResponseEntity<BaseResponse<List<DepartmentNameDto>>> getSupportedDepartments() {
        List<DepartmentNameDto> departmentNames = noticeService.lookupSupportedDepartments();
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SEARCH_SUCCESS, departmentNames));
    }
}
