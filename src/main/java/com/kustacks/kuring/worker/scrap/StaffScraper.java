package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.staff.common.dto.StaffDto;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.client.staff.StaffApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.parser.staff.StaffHtmlParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class StaffScraper {

    private static final int RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도

    private final List<StaffHtmlParser> htmlParsers;
    private final List<StaffApiClient> staffApiClients;

    public StaffScraper(List<StaffHtmlParser> htmlParsers, List<StaffApiClient> staffApiClients) {
        this.htmlParsers = htmlParsers;
        this.staffApiClients = staffApiClients;
    }

    @Retryable(value = {InternalLogicException.class}, backoff = @Backoff(delay = RETRY_PERIOD))
    public List<StaffDto> scrap(DeptInfo deptInfo) throws InternalLogicException {

        List<Document> documents = null;
        List<StaffDto> staffDtoList = new LinkedList<>();

        for (StaffApiClient staffApiClient : staffApiClients) {
            if (staffApiClient.support(deptInfo)) {
                log.info("{} HTML 요청", deptInfo.getDeptName());
                documents = staffApiClient.getHTML(deptInfo);
                log.info("{} HTML 수신", deptInfo.getDeptName());
            }
        }

        // 수신한 documents HTML 파싱
        List<String[]> parseResult = new LinkedList<>();
        for (StaffHtmlParser htmlParser : htmlParsers) {
            if (htmlParser.support(deptInfo)) {
                log.info("{} HTML 파싱 시작", deptInfo.getDeptName());
                for (Document document : documents) {
                    parseResult.addAll(htmlParser.parse(document));
                }
                log.info("{} HTML 파싱 완료", deptInfo.getDeptName());
            }
        }

        // 파싱 결과를 staffDto로 변환
        for (String[] oneStaffInfo : parseResult) {
            staffDtoList.add(StaffDto.builder()
                    .name(oneStaffInfo[0])
                    .major(oneStaffInfo[1])
                    .lab(oneStaffInfo[2])
                    .phone(oneStaffInfo[3])
                    .email(oneStaffInfo[4])
                    .deptName(deptInfo.getDeptName())
                    .collegeName(deptInfo.getCollegeName()).build());
        }

        if (staffDtoList.isEmpty()) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP);
        }

        return staffDtoList;
    }
}
