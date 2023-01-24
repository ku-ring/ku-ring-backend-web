package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.response.NoticeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/notice", produces = "application/json")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public NoticeListResponse getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "offset") @Min(0) int offset,
            @RequestParam(name = "max") @Min(1) @Max(30) int max) {
        return noticeService.getNotices(type, offset, max);
    }
}
