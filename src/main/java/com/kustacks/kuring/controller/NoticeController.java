package com.kustacks.kuring.controller;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.controller.dto.NoticeResponseDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.NoticeCategory;
import com.kustacks.kuring.service.NoticeService;
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

    @Value("${notice.base-url}")
    private String baseUrl;

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notice")
    public NoticeResponseDTO getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "offset") @Min(0) int offset,
            @RequestParam(name = "max") @Min(1) @Max(10) int max) {

        String categoryName = "";
        for (NoticeCategory noticeCategory : NoticeCategory.values()) {
            if(noticeCategory.getShortName().equals(type)) {
                categoryName = noticeCategory.getName();
                break;
            }
        }
        if(categoryName.equals("")) {
            throw new InternalLogicException(ErrorCode.API_NOTICE_CANNOT_FIND_CATEGORY);
        }

        List<NoticeDTO> notices = noticeService.getNotices(categoryName, offset, max);
        if(notices == null) {
            throw new InternalLogicException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY);
        }

        return new NoticeResponseDTO(baseUrl, notices);
    }
}
