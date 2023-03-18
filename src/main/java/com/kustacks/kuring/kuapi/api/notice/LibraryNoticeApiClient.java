package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.utils.converter.DTOConverter;
import com.kustacks.kuring.common.utils.converter.LibraryNoticeDTOToCommonFormatDTOConverter;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.LibraryNoticeDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.LibraryNoticeResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LibraryNoticeApiClient implements NoticeApiClient {

    private static final int MAX_REQUEST_COUNT = 2;
    private static final int OFFSET = 0;
    private static final int MAX = 20;
    private static final int MIN = 1;

    private final DTOConverter dtoConverter;
    private final RestTemplate restTemplate;

    @Value("${library.request-url}")
    private String libraryUrl;

    public LibraryNoticeApiClient(LibraryNoticeDTOToCommonFormatDTOConverter dtoConverter, RestTemplate restTemplate) {
        this.dtoConverter = dtoConverter;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InternalLogicException {
        List<LibraryNoticeDTO> libraryNoticeDtoList = scrapLibraryNoticeDtos();
        return convertToCommonFormatDto(libraryNoticeDtoList);
    }

    private List<LibraryNoticeDTO> scrapLibraryNoticeDtos() {
        String completeLibraryUrl = buildUrl(libraryUrl, OFFSET, MAX);

        for (int requestIndex = 0; requestIndex < MAX_REQUEST_COUNT; requestIndex++) {
            LibraryNoticeResponseDTO libraryNoticeResponseDTO = restTemplate
                    .getForEntity(completeLibraryUrl, LibraryNoticeResponseDTO.class)
                    .getBody();

            validateResponse(requestIndex, libraryNoticeResponseDTO);

            if (isValidSize(libraryNoticeResponseDTO)) {
                return libraryNoticeResponseDTO.getList();
            }
        }

        return Collections.emptyList();
    }

    private static boolean isValidSize(LibraryNoticeResponseDTO libraryNoticeResponseDTO) {
        return libraryNoticeResponseDTO.getTotalCount() >= MIN && libraryNoticeResponseDTO.getTotalCount() <= MAX;
    }

    private String buildUrl(String url, int offset, int max) {
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("offset", offset)
                .queryParam("max", max)
                .build().toString();
    }

    private List<CommonNoticeFormatDTO> convertToCommonFormatDto(List<LibraryNoticeDTO> libraryNoticeDtoList) {
        return libraryNoticeDtoList.stream()
                .map(dto -> (CommonNoticeFormatDTO) dtoConverter.convert(dto))
                .collect(Collectors.toList());
    }

    private void validateResponse(int requestIndex, LibraryNoticeResponseDTO libraryNoticeResponseDTO) {
        if (libraryNoticeResponseDTO == null) {
            log.error("도서관 공지 {}번째 요청에 대한 응답의 body가 없습니다.", requestIndex + 1);
            throw new InternalLogicException(ErrorCode.LIB_CANNOT_PARSE_JSON);
        }

        if (!libraryNoticeResponseDTO.isSuccess()) {
            log.error("도서관 공지 {}번째 요청에 대한 응답이 fail입니다.", requestIndex + 1);
            throw new InternalLogicException(ErrorCode.LIB_BAD_RESPONSE);
        }
    }
}

