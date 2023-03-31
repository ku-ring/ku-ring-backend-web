package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.DepartmentListResponse;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeListLookupResponse;
import com.kustacks.kuring.notice.common.dto.NoticeLookupResponse;
import com.kustacks.kuring.search.common.dto.NoticeSearchDto;
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

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_SEARCH_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeControllerV2 {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<BaseResponse<NoticeListLookupResponse>> getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "offset") @Min(0) int offset,
            @RequestParam(name = "max") @Min(1) @Max(30) int max) {
        List<NoticeDto> searchResults = noticeService.getNoticesV2(type, department, offset, max);
        return ResponseEntity.ok().body(new BaseResponse(NOTICE_SEARCH_SUCCESS, new NoticeListLookupResponse(searchResults)));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<NoticeLookupResponse>> searchNotice(@NotBlank @RequestParam String content) {
        List<NoticeSearchDto> noticeDtoList = noticeService.findAllNoticeByContent(content);
        NoticeLookupResponse response = new NoticeLookupResponse(noticeDtoList);
        return ResponseEntity.ok().body(new BaseResponse(NOTICE_SEARCH_SUCCESS, response));
    }

    @GetMapping("/departments")
    public ResponseEntity<BaseResponse<DepartmentListResponse>> getSupportedDepartments() {
        List<DepartmentNameDto> departmentNames = noticeService.lookupSupportedDepartments();
        DepartmentListResponse response = new DepartmentListResponse(departmentNames);
        return ResponseEntity.ok().body(new BaseResponse(DEPARTMENTS_SEARCH_SUCCESS, response));
    }
}
