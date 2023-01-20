package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.common.dto.NoticeDto;
import com.kustacks.kuring.common.dto.NoticeResponseDto;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.notice.business.NoticeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class NoticeController {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notice")
    public NoticeResponseDto getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "offset") @Min(0) int offset,
            @RequestParam(name = "max") @Min(1) @Max(30) int max) {

        String categoryName = "";
        for (CategoryName noticeCategory : CategoryName.values()) {
            if(noticeCategory.getShortName().equals(type)) {
                categoryName = noticeCategory.getName();
                break;
            }
        }
        if(categoryName.equals("")) {
            throw new APIException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY);
        }

        List<NoticeDto> notices = noticeService.getNotices(categoryName, offset, max);
        if(notices == null) {
            throw new APIException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY);
        }

        return new NoticeResponseDto(type.equals(CategoryName.LIBRARY.getShortName()) ? libraryBaseUrl : normalBaseUrl, notices);
    }
}
