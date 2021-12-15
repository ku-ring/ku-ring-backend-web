package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.LibraryNoticeDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.LibraryNoticeResponseDTO;
import com.kustacks.kuring.util.converter.DTOConverter;
import com.kustacks.kuring.util.converter.LibraryNoticeDTOToCommonFormatDTOConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;

@Component
public class LibraryNoticeAPIClient implements NoticeAPIClient {

    @Value("${library.request-url}")
    private String libraryUrl;

    private final DTOConverter dtoConverter;

    public LibraryNoticeAPIClient(LibraryNoticeDTOToCommonFormatDTOConverter dtoConverter) {
        this.dtoConverter = dtoConverter;
    }

    /*
        도서관 공지 갱신
     */

    @Override
    public List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) {

        RestTemplate restTemplate = new RestTemplate();

        int offset = 0;
        int max = 20;
        int reqIdx = 0;
        List<LibraryNoticeDTO> libraryNoticeDTOList = new LinkedList<>();
        while(reqIdx < 2) {
            String fullLibraryUrl = UriComponentsBuilder.fromUriString(libraryUrl).queryParam("offset", offset).queryParam("max", max).build().toString();
            ResponseEntity<LibraryNoticeResponseDTO> libraryResponse = restTemplate.getForEntity(fullLibraryUrl, LibraryNoticeResponseDTO.class);
            LibraryNoticeResponseDTO libraryNoticeResponseDTO = libraryResponse.getBody();
            if(libraryNoticeResponseDTO == null) {
//                log.error("도서관 공지 {}번째 요청에 대한 응답의 body가 없습니다.", reqIdx + 1);
                throw new InternalLogicException(ErrorCode.LIB_CANNOT_PARSE_JSON);
            }

            boolean isLibraryRequestSuccess = libraryNoticeResponseDTO.isSuccess();
            if(!isLibraryRequestSuccess) {
//                log.error("도서관 공지 {}번째 요청에 대한 응답이 fail입니다.", reqIdx + 1);
                throw new InternalLogicException(ErrorCode.LIB_BAD_RESPONSE);
            }

            offset = max;
            max = libraryNoticeResponseDTO.getData().getTotalCount() - max;

            libraryNoticeDTOList.addAll(libraryNoticeResponseDTO.getData().getList());
            ++reqIdx;
        }

        return convertToCommonFormatDTO(libraryNoticeDTOList);
    }

    private List<CommonNoticeFormatDTO> convertToCommonFormatDTO(List<LibraryNoticeDTO> libraryNoticeDTOList) {

        List<CommonNoticeFormatDTO> ret = new LinkedList<>();
        for (LibraryNoticeDTO libraryNoticeDTO : libraryNoticeDTOList) {
            ret.add((CommonNoticeFormatDTO) dtoConverter.convert(libraryNoticeDTO));
        }

        return ret;
    }
}
