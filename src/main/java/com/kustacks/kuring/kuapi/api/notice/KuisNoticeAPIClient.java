package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.notice.KuisAuthManager;
import com.kustacks.kuring.kuapi.notice.RenewSessionKuisAuthManager;
import com.kustacks.kuring.kuapi.notice.dto.request.*;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisRequestBody;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.KuisNoticeDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.KuisNoticeResponseDTO;
import com.kustacks.kuring.util.converter.DTOConverter;
import com.kustacks.kuring.util.converter.KuisNoticeDTOToCommonFormatDTOConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class KuisNoticeAPIClient implements NoticeAPIClient {

    @Value("${notice.referer}")
    private String noticeReferer;

    @Value("${notice.request-url}")
    private String noticeUrl;

    private final DTOConverter dtoConverter;
    private final KuisAuthManager kuisAuthManager;
    private final Map<CategoryName, KuisNoticeRequestBody> noticeRequestBodies;

    public KuisNoticeAPIClient(RenewSessionKuisAuthManager kuisAuthManager,
                               KuisNoticeDTOToCommonFormatDTOConverter dtoConverter,

                               BachelorKuisNoticeRequestBody bachelorNoticeRequestBody,
                               ScholarshipKuisNoticeRequestBody scholarshipNoticeRequestBody,
                               EmploymentKuisNoticeRequestBody employmentKuisNoticeRequestBody,
                               NationalKuisNoticeRequestBody nationalKuisNoticeRequestBody,
                               StudentKuisNoticeRequestBody studentKuisNoticeRequestBody,
                               IndustryUnivKuisNoticeRequestBody industryUnivKuisNoticeRequestBody,
                               NormalKuisNoticeRequestBody normalKuisNoticeRequestBody) {

        this.dtoConverter = dtoConverter;
        this.kuisAuthManager = kuisAuthManager;

        noticeRequestBodies = new HashMap<>();
        noticeRequestBodies.put(CategoryName.BACHELOR, bachelorNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.SCHOLARSHIP, scholarshipNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.EMPLOYMENT, employmentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NATIONAL, nationalKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.STUDENT, studentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.INDUSTRY_UNIV, industryUnivKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NORMAL, normalKuisNoticeRequestBody);
    }

    @Override
    public List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InternalLogicException, InterruptedException {

        // sessionId 획득
        String sessionId;
        try {
            sessionId = kuisAuthManager.getSessionId();
        } catch (InternalLogicException | InterruptedException e) {
            throw e;
        }

        // 공지 요청 헤더
        HttpHeaders noticeRequestHeader = createKuisNoticeRequestHeader(sessionId);

        RestTemplate restTemplate = new RestTemplate();

        // 공지 요청
        KuisNoticeRequestBody kuisNoticeRequestBody = noticeRequestBodies.get(categoryName);

        String encodedNoticeRequestBody = KuisRequestBody.toUrlEncodedString(kuisNoticeRequestBody);
        HttpEntity<String> noticeRequestEntity = new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
        ResponseEntity<KuisNoticeResponseDTO> noticeResponse = restTemplate.exchange(noticeUrl, HttpMethod.POST, noticeRequestEntity, KuisNoticeResponseDTO.class);

        KuisNoticeResponseDTO body = noticeResponse.getBody();
        if(body == null) {
//                log.error("{} 공지 요청에 대한 응답의 body가 없습니다.", noticeCategory.getName());
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        }

        List<KuisNoticeDTO> kuisNoticeDTOList = noticeResponse.getBody().getKuisNoticeDTOList();
        if(kuisNoticeDTOList == null) {
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        } else {
            // common format으로 변환
            return convertToCommonFormatDTO(kuisNoticeDTOList);
        }
    }

    private HttpHeaders createKuisNoticeRequestHeader(String sessionId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", noticeReferer);
        httpHeaders.add("Cookie", sessionId);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }

    private List<CommonNoticeFormatDTO> convertToCommonFormatDTO(List<KuisNoticeDTO> kuisNoticeDTOList) {

        List<CommonNoticeFormatDTO> converted = new LinkedList<>();
        for (KuisNoticeDTO kuisNoticeDTO : kuisNoticeDTOList) {
            converted.add((CommonNoticeFormatDTO) dtoConverter.convert(kuisNoticeDTO));
        }

        return converted;
    }
}
