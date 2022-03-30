package com.kustacks.kuring.controller;

import com.kustacks.kuring.CategoryName;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.controller.dto.NoticeResponseDTO;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.service.NoticeService;
import com.kustacks.kuring.service.NoticeServiceImpl;
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

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeServiceImpl) {
        this.noticeService = noticeServiceImpl;
    }

    @Deprecated
    @GetMapping("/notice")
    public NoticeResponseDTO getNotices(
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

        // Spring Data JPA에서 findBy... 쿼리를 사용 시 결과가 없으면, null이 리턴됨
        // 따라서 notices == null이면 오류가 아니라 단순히 결과값이 없는 상태라고 해석하기로 했음
        List<NoticeDTO> notices = noticeService.getNotices(categoryName, offset, max);

        // TODO: baseUrl은 클라이언트 앱 레거시 버전 사용자가 거의 없어질 때 없앨 예정
        if(notices.isEmpty()) {
            return new NoticeResponseDTO(notices);
        } else {
            String baseUrl = "";

            if(CategoryName.LIBRARY.getName().equals(categoryName)) {
                baseUrl = libraryBaseUrl;
            } else if(CategoryName.BACHELOR.getName().equals(categoryName) ||
                    CategoryName.SCHOLARSHIP.getName().equals(categoryName) ||
                    CategoryName.EMPLOYMENT.getName().equals(categoryName) ||
                    CategoryName.NATIONAL.getName().equals(categoryName) ||
                    CategoryName.STUDENT.getName().equals(categoryName) ||
                    CategoryName.INDUSTRY_UNIV.getName().equals(categoryName) ||
                    CategoryName.NORMAL.getName().equals(categoryName)
            ) {
                baseUrl = normalBaseUrl;
            }

            return new NoticeResponseDTO(baseUrl, notices);
        }
    }
}
