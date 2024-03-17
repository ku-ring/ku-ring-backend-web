package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.utils.converter.KuisNoticeDtoToCommonFormatDtoConverter;
import com.kustacks.kuring.worker.scrap.client.auth.ParsingKuisAuthManager;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisNoticeProperties;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KuisNoticeApiClient implements NoticeApiClient<CommonNoticeFormatDto, KuisNoticeInfo> {

    private final KuisNoticeDtoToCommonFormatDtoConverter dtoConverter;
    private final ParsingKuisAuthManager parsingKuisAuthManager;
    private final KuisNoticeProperties kuisNoticeProperties;
    private final RestTemplate restTemplate;

    @Override
    @Retryable(value = {InternalLogicException.class})
    public List<CommonNoticeFormatDto> request(KuisNoticeInfo kuisNoticeRequestBody) throws InternalLogicException {
        // sessionId 획득
        String sessionId = parsingKuisAuthManager.getSessionId();

        // 공지 요청 헤더
        HttpHeaders noticeRequestHeader = createKuisNoticeRequestHeader(sessionId);

        // 공지 요청 엔티티 생성
        HttpEntity<String> noticeRequestEntity = kuisNoticeRequests(kuisNoticeRequestBody, noticeRequestHeader);

        // 공지 요청 전송과 반환 Dto 생성
        ResponseEntity<KuisNoticeResponseDto> noticeResponse = sendKuisNoticesRequestAndResponse(noticeRequestEntity);

        return convertToCommonFormatDto(validateResponse(noticeResponse));
    }

    @Override
    public List<CommonNoticeFormatDto> requestAll(KuisNoticeInfo kuisNoticeRequestBody) throws InternalLogicException {
        return Collections.emptyList();
    }

    private HttpEntity<String> kuisNoticeRequests(KuisNoticeInfo kuisNoticeRequestBody, HttpHeaders noticeRequestHeader) {
        String encodedNoticeRequestBody = KuisInfo.toUrlEncodedString(kuisNoticeRequestBody);
        return new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
    }

    private ResponseEntity<KuisNoticeResponseDto> sendKuisNoticesRequestAndResponse(
            HttpEntity<String> noticeRequestEntity
    ) throws InternalLogicException {
        try {
            return restTemplate.postForEntity(
                    kuisNoticeProperties.requestUrl(),
                    noticeRequestEntity,
                    KuisNoticeResponseDto.class);
        } catch (RestClientException e) {
            log.warn("세션 갱신이 필요합니다.");
            parsingKuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE, e);
        }
    }

    private List<KuisNoticeDto> validateResponse(ResponseEntity<KuisNoticeResponseDto> noticeResponse) {
        KuisNoticeResponseDto body = noticeResponse.getBody();
        if (body == null) {
            parsingKuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        }

        List<KuisNoticeDto> kuisNoticeDtoList = body.getKuisNoticeDtoList();
        if (kuisNoticeDtoList == null) {
            parsingKuisAuthManager.forceRenewing();
            throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
        }

        return kuisNoticeDtoList;
    }

    private HttpHeaders createKuisNoticeRequestHeader(String sessionId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.REFERER, kuisNoticeProperties.refererUrl());
        httpHeaders.add(HttpHeaders.COOKIE, sessionId);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }

    private List<CommonNoticeFormatDto> convertToCommonFormatDto(List<KuisNoticeDto> kuisNoticeDtoList) {
        return kuisNoticeDtoList.stream()
                .map(dto -> (CommonNoticeFormatDto) dtoConverter.convert(dto))
                .collect(Collectors.toList());
    }
}
