package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.utils.converter.DtoConverter;
import com.kustacks.kuring.common.utils.converter.KuisNoticeDtoToCommonFormatDtoConverter;
import com.kustacks.kuring.worker.scrap.client.auth.KuisAuthManager;
import com.kustacks.kuring.worker.update.notice.dto.request.BachelorKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.EmploymentKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.IndustryUnivKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.NationalKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.NormalKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.ScholarshipKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.StudentKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KuisNoticeApiClient implements NoticeApiClient<CommonNoticeFormatDto, CategoryName> {

    @Value("${notice.referer}")
    private String noticeReferer;

    @Value("${notice.request-url}")
    private String noticeUrl;

    private final RestTemplate restTemplate;
    private final DtoConverter dtoConverter;
    private final KuisAuthManager kuisAuthManager;
    private final Map<CategoryName, KuisNoticeRequestBody> noticeRequestBodies;

    public KuisNoticeApiClient(KuisAuthManager parsingKuisAuthManager,
                               KuisNoticeDtoToCommonFormatDtoConverter dtoConverter,

                               BachelorKuisNoticeRequestBody bachelorNoticeRequestBody,
                               ScholarshipKuisNoticeRequestBody scholarshipNoticeRequestBody,
                               EmploymentKuisNoticeRequestBody employmentKuisNoticeRequestBody,
                               NationalKuisNoticeRequestBody nationalKuisNoticeRequestBody,
                               StudentKuisNoticeRequestBody studentKuisNoticeRequestBody,
                               IndustryUnivKuisNoticeRequestBody industryUnivKuisNoticeRequestBody,
                               NormalKuisNoticeRequestBody normalKuisNoticeRequestBody,

                               RestTemplate restTemplate) {

        this.dtoConverter = dtoConverter;
        this.kuisAuthManager = parsingKuisAuthManager;

        this.restTemplate = restTemplate;

        noticeRequestBodies = new HashMap<>();
        noticeRequestBodies.put(CategoryName.BACHELOR, bachelorNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.SCHOLARSHIP, scholarshipNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.EMPLOYMENT, employmentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NATIONAL, nationalKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.STUDENT, studentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.INDUSTRY_UNIVERSITY, industryUnivKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NORMAL, normalKuisNoticeRequestBody);
    }

    @Override
    @Retryable(value = {InternalLogicException.class})
    public List<CommonNoticeFormatDto> request(CategoryName categoryName) throws InternalLogicException {

        // sessionId 획득
        String sessionId = kuisAuthManager.getSessionId();

        // 공지 요청 헤더
        HttpHeaders noticeRequestHeader = createKuisNoticeRequestHeader(sessionId);

        // 공지 요청 엔티티 생성
        HttpEntity<String> noticeRequestEntity = kuisNoticeRequests(categoryName, noticeRequestHeader);

        // 공지 요청 전송과 반환 Dto 생성
        ResponseEntity<KuisNoticeResponseDto> noticeResponse = sendKuisNoticesRequestAndResponse(noticeRequestEntity);

        List<KuisNoticeDto> kuisNoticeDtoList = validateResponse(noticeResponse);

        return convertToCommonFormatDto(kuisNoticeDtoList);
    }

    @Override
    public List<CommonNoticeFormatDto> requestAll(CategoryName categoryName) throws InternalLogicException {
        return Collections.emptyList();
    }

    private HttpEntity<String> kuisNoticeRequests(CategoryName categoryName, HttpHeaders noticeRequestHeader) {
        KuisNoticeRequestBody kuisNoticeRequestBody = noticeRequestBodies.get(categoryName);
        String encodedNoticeRequestBody = KuisRequestBody.toUrlEncodedString(kuisNoticeRequestBody);

        return new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
    }

    private ResponseEntity<KuisNoticeResponseDto> sendKuisNoticesRequestAndResponse(HttpEntity<String> noticeRequestEntity) {
        ResponseEntity<KuisNoticeResponseDto> noticeResponse;
        try {
            noticeResponse = restTemplate.postForEntity(noticeUrl, noticeRequestEntity, KuisNoticeResponseDto.class);
        } catch (RestClientException e) {
            log.warn("세션 갱신이 필요합니다.");
            kuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE, e);
        }
        return noticeResponse;
    }

    private List<KuisNoticeDto> validateResponse(ResponseEntity<KuisNoticeResponseDto> noticeResponse) {
        KuisNoticeResponseDto body = noticeResponse.getBody();
        if (body == null) {
            kuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        }

        List<KuisNoticeDto> kuisNoticeDtoList = body.getKuisNoticeDtoList();
        if (kuisNoticeDtoList == null) {
            kuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        }

        return kuisNoticeDtoList;
    }

    private HttpHeaders createKuisNoticeRequestHeader(String sessionId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.REFERER, noticeReferer);
        httpHeaders.add(HttpHeaders.COOKIE, sessionId);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }

    private List<CommonNoticeFormatDto> convertToCommonFormatDto(List<KuisNoticeDto> kuisNoticeDtoList) {
        List<CommonNoticeFormatDto> converted = new LinkedList<>();
        for (KuisNoticeDto kuisNoticeDTO : kuisNoticeDtoList) {
            converted.add((CommonNoticeFormatDto) dtoConverter.convert(kuisNoticeDTO));
        }

        return converted;
    }
}
