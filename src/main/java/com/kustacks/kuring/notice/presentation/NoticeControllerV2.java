package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.NoticeLookupResponse;
import com.kustacks.kuring.search.common.dto.response.NoticeSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_SEARCH_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeControllerV2 {

    private final NoticeService noticeService;

    @GetMapping("/notices/search")
    public ResponseEntity<BaseResponse<NoticeLookupResponse>> searchNotice(@NotBlank @RequestParam String content) {
        List<NoticeSearchDto> noticeDtoList = noticeService.findAllNoticeByContent(content);
        NoticeLookupResponse response = new NoticeLookupResponse(noticeDtoList);
        return ResponseEntity.ok().body(new BaseResponse(NOTICE_SEARCH_SUCCESS, response));
    }
}
