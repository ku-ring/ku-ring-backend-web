package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.utils.converter.DtoConverter;
import com.kustacks.kuring.common.utils.converter.LibraryNoticeDtoToCommonFormatDtoConverter;
import com.kustacks.kuring.worker.scrap.client.notice.property.LibraryNoticeProperties;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.LibraryNoticeDto;
import com.kustacks.kuring.worker.update.notice.dto.response.LibraryNoticeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class LibraryNoticeApiClient implements NoticeApiClient<CommonNoticeFormatDto, CategoryName> {

    private static final int MAX_REQUEST_COUNT = 2;

    private final DtoConverter dtoConverter;
    private final RestTemplate restTemplate;
    private final LibraryNoticeProperties libraryNoticeProperties;

    public LibraryNoticeApiClient(LibraryNoticeDtoToCommonFormatDtoConverter dtoConverter, RestTemplate restTemplate, LibraryNoticeProperties libraryNoticeProperties) {
        this.dtoConverter = dtoConverter;
        this.restTemplate = restTemplate;
        this.libraryNoticeProperties = libraryNoticeProperties;
    }

    @Override
    public List<CommonNoticeFormatDto> request(CategoryName categoryName) throws InternalLogicException {
        List<LibraryNoticeDto> libraryNoticeDtoList = scrapLibraryNoticeDtos();
        return convertToCommonFormatDto(libraryNoticeDtoList);
    }

    @Override
    public List<CommonNoticeFormatDto> requestAll(CategoryName categoryName) throws InternalLogicException {
        return Collections.emptyList();
    }

    @Override
    public CommonNoticeFormatDto requestSinglePageWithUrl(CategoryName noticeInfo, String url) {
        throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE);
    }

    private List<LibraryNoticeDto> scrapLibraryNoticeDtos() {
        int offset = 0;
        int max = 20;

        List<LibraryNoticeDto> libraryNoticeDtoList = new LinkedList<>();
        for (int requestIndex = 0; requestIndex < MAX_REQUEST_COUNT; requestIndex++) {
            String completeLibraryUrl = buildUrl(libraryNoticeProperties.requestUrl(), offset, max);
            LibraryNoticeResponseDto libraryNoticeResponseDto = restTemplate
                    .getForEntity(completeLibraryUrl, LibraryNoticeResponseDto.class)
                    .getBody();

            validateResponse(requestIndex, libraryNoticeResponseDto);

            offset = max;
            max = libraryNoticeResponseDto.getTotalCount() - max;

            libraryNoticeDtoList.addAll(libraryNoticeResponseDto.getData().getList());
        }

        return libraryNoticeDtoList;
    }

    private String buildUrl(String url, int offset, int max) {
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("offset", offset)
                .queryParam("max", max)
                .build().toString();
    }

    private List<CommonNoticeFormatDto> convertToCommonFormatDto(List<LibraryNoticeDto> libraryNoticeDtoList) {
        return libraryNoticeDtoList.stream()
                .map(dto -> (CommonNoticeFormatDto) dtoConverter.convert(dto))
                .toList();
    }

    private void validateResponse(int requestIndex, LibraryNoticeResponseDto libraryNoticeResponseDto) {
        if (libraryNoticeResponseDto == null) {
            log.error("도서관 공지 {}번째 요청에 대한 응답의 body가 없습니다.", requestIndex + 1);
            throw new InternalLogicException(ErrorCode.LIB_CANNOT_PARSE_JSON);
        }

        if (!libraryNoticeResponseDto.isSuccess()) {
            log.error("도서관 공지 {}번째 요청에 대한 응답이 fail입니다.", requestIndex + 1);
            throw new InternalLogicException(ErrorCode.LIB_BAD_RESPONSE);
        }
    }
}

