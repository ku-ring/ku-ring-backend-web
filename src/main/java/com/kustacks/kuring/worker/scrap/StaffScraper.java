package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.scrap.client.staff.StaffApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.parser.staff.StaffHtmlParserTemplate;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StaffScraper {

    private static final int RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도

    private final List<StaffHtmlParserTemplate> htmlParsers;
    private final List<StaffApiClient> staffApiClients;

    public StaffScraper(List<StaffHtmlParserTemplate> htmlParsers, List<StaffApiClient> staffApiClients) {
        this.htmlParsers = htmlParsers;
        this.staffApiClients = staffApiClients;
    }

    @Retryable(value = {InternalLogicException.class}, backoff = @Backoff(delay = RETRY_PERIOD))
    public List<StaffDto> scrap(DeptInfo deptInfo) throws InternalLogicException {
        log.debug("{} HTML 요청", deptInfo.getDeptName());
        List<Document> documents = requestHtmlByStaffApiClient(deptInfo);
        log.debug("{} HTML 응답 도착", deptInfo.getDeptName());

        StaffHtmlParserTemplate htmlParser = findHtmlParser(deptInfo);

        log.debug("{} HTML 파싱 시작", deptInfo.getDeptName());
        List<String[]> parseResult = parseHtmlDocuments(documents, htmlParser);
        log.debug("{} HTML 파싱 완료", deptInfo.getDeptName());

        List<StaffDto> staffDtoList = convertStaffDtos(deptInfo, parseResult);

        if (staffDtoList.isEmpty()) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP);
        }

        return staffDtoList;
    }

    private static List<String[]> parseHtmlDocuments(List<Document> documents, StaffHtmlParserTemplate htmlParser) {
        return documents.stream()
                .map(htmlParser::parse)
                .flatMap(List::stream)
                .toList();
    }

    private static List<StaffDto> convertStaffDtos(DeptInfo deptInfo, List<String[]> parseResult) {
        return parseResult.stream()
                .map(oneStaffInfo -> StaffDto.builder()
                        .name(oneStaffInfo[0])
                        .position(oneStaffInfo[1])
                        .major(oneStaffInfo[2])
                        .lab(oneStaffInfo[3])
                        .phone(oneStaffInfo[4])
                        .email(oneStaffInfo[5])
                        .deptName(deptInfo.getDeptName())
                        .collegeName(deptInfo.getCollegeName()
                        ).build()
                ).toList();
    }

    private StaffHtmlParserTemplate findHtmlParser(DeptInfo deptInfo) {
        return htmlParsers.stream()
                .filter(htmlParser -> htmlParser.support(deptInfo))
                .findFirst()
                .orElseThrow(() -> new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP));
    }

    private List<Document> requestHtmlByStaffApiClient(DeptInfo deptInfo) {
        return staffApiClients.stream()
                .filter(staffApiClient -> staffApiClient.support(deptInfo))
                .findFirst()
                .map(staffApiClient -> staffApiClient.getHTML(deptInfo))
                .orElseThrow(() -> new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP));
    }
}
