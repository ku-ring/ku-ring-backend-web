package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class KuisHomepageNoticeScraperTemplate {

    public List<ComplexNoticeFormatDto> scrap(KuisHomepageNoticeInfo kuisNoticeInfo, Function<KuisHomepageNoticeInfo, List<ScrapingResultDto>> decisionMaker) throws InternalLogicException {
        List<ScrapingResultDto> requestResults = requestWithDeptInfo(kuisNoticeInfo, decisionMaker);

        log.info("[{}] HTML 파싱 시작", kuisNoticeInfo.getCategoryName());
        List<ComplexNoticeFormatDto> noticeDtoList = htmlParsingFromScrapingResult(kuisNoticeInfo, requestResults);
        log.info("[{}] HTML 파싱 완료", kuisNoticeInfo.getCategoryName());

        for (ComplexNoticeFormatDto complexNoticeFormatDto : noticeDtoList) {
            if (complexNoticeFormatDto.getNormalNoticeSize() == 0) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP);
            }
        }

        return noticeDtoList;
    }

    private List<ScrapingResultDto> requestWithDeptInfo(KuisHomepageNoticeInfo kuisNoticeInfo, Function<KuisHomepageNoticeInfo, List<ScrapingResultDto>> decisionMaker) {
        long startTime = System.currentTimeMillis();

        log.info("[{}] HTML 요청", kuisNoticeInfo.getCategoryName());
        List<ScrapingResultDto> reqResults = decisionMaker.apply(kuisNoticeInfo);
        log.info("[{}] HTML 수신", kuisNoticeInfo.getCategoryName());

        long endTime = System.currentTimeMillis();
        log.info("[{}] 파싱에 소요된 초 = {}", kuisNoticeInfo.getCategoryName(), (endTime - startTime) / 1000.0);

        return reqResults;
    }


    private List<ComplexNoticeFormatDto> htmlParsingFromScrapingResult(KuisHomepageNoticeInfo kuisNoticeInfo, List<ScrapingResultDto> requestResults) {
        List<ComplexNoticeFormatDto> noticeDtoList = new LinkedList<>();
        for (ScrapingResultDto reqResult : requestResults) {
            Document document = reqResult.getDocument();
            String viewUrl = reqResult.getViewUrl();

            RowsDto rowsDto = kuisNoticeInfo.parse(document);
            List<CommonNoticeFormatDto> importantNoticeFormatDtos = rowsDto.buildImportantRowList(viewUrl);
            List<CommonNoticeFormatDto> normalNoticeFormatDtos = rowsDto.buildNormalRowList(viewUrl);
            log.info("[{}] 공지 개수 = {}", kuisNoticeInfo.getCategoryName(), importantNoticeFormatDtos.size() + normalNoticeFormatDtos.size());
            noticeDtoList.add(new ComplexNoticeFormatDto(importantNoticeFormatDtos, normalNoticeFormatDtos));
        }

        return noticeDtoList;
    }
}
