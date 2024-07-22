package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class KuisHomepageNoticeScraperTemplate {

    public List<ComplexNoticeFormatDto> scrap(
            KuisHomepageNoticeInfo kuisNoticeInfo,
            Function<KuisHomepageNoticeInfo, List<ScrapingResultDto>> decisionMaker
    ) throws InternalLogicException {
        List<ScrapingResultDto> requestResults = requestWithDeptInfo(kuisNoticeInfo, decisionMaker);

        log.debug("[{}] HTML 파싱 시작", kuisNoticeInfo.getCategoryName());
        List<ComplexNoticeFormatDto> noticeDtoList = htmlParsingFromScrapingResult(kuisNoticeInfo, requestResults);
        log.debug("[{}] HTML 파싱 완료", kuisNoticeInfo.getCategoryName());

        validateScrapedNoticeCountIsNotZero(noticeDtoList);

        return noticeDtoList;
    }

    public List<PageTextDto> scrapForEmbedding(
            List<NoticeDto> scrapResults,
            KuisHomepageNoticeInfo noticeInfo
    ) throws InternalLogicException {
        List<ScrapingResultDto> requestResults = requestWithDeptInfoForEmbedding(scrapResults, noticeInfo);

        log.debug("[{}] Text extract begin", noticeInfo.getCategoryName());
        List<PageTextDto> noticeDtoList = htmlTextParsingFromScrapingResult(noticeInfo, requestResults);
        log.debug("[{}] Text extract end", noticeInfo.getCategoryName());

        return noticeDtoList;
    }

    private void validateScrapedNoticeCountIsNotZero(List<ComplexNoticeFormatDto> noticeDtoList) {
        for (ComplexNoticeFormatDto complexNoticeFormatDto : noticeDtoList) {
            if (complexNoticeFormatDto.getNormalNoticeSize() == 0) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP);
            }
        }
    }

    private List<ScrapingResultDto> requestWithDeptInfo(
            KuisHomepageNoticeInfo kuisNoticeInfo,
            Function<KuisHomepageNoticeInfo, List<ScrapingResultDto>> decisionMaker
    ) {
        long startTime = System.currentTimeMillis();

        log.debug("[{}] HTML SCRAP 요청", kuisNoticeInfo.getCategoryName());
        List<ScrapingResultDto> reqResults = decisionMaker.apply(kuisNoticeInfo);
        log.debug("[{}] HTML SCRAP 수신", kuisNoticeInfo.getCategoryName());

        long endTime = System.currentTimeMillis();
        log.debug("[{}] 파싱에 소요된 초 = {}", kuisNoticeInfo.getCategoryName(), (endTime - startTime) / 1000.0);

        return reqResults;
    }

    private List<ScrapingResultDto> requestWithDeptInfoForEmbedding(
            List<NoticeDto> scrapResults,
            KuisHomepageNoticeInfo noticeInfo
    ) {
        long startTime = System.currentTimeMillis();

        List<ScrapingResultDto> scrapResultDtos = new LinkedList<>();
        for (NoticeDto scrapResult : scrapResults) {
            log.debug("[{}] HTML SCRAP 요청", noticeInfo.getCategoryName());
            scrapResultDtos.add(noticeInfo.scrapSinglePageHtml(scrapResult.getUrl()));
            log.debug("[{}] HTML SCRAP 수신", noticeInfo.getCategoryName());
        }

        long endTime = System.currentTimeMillis();
        log.debug("[{}] 파싱에 소요된 초 = {}", noticeInfo.getCategoryName(), (endTime - startTime) / 1000.0);

        return scrapResultDtos;
    }

    private List<PageTextDto> htmlTextParsingFromScrapingResult(
            KuisHomepageNoticeInfo noticeInfo,
            List<ScrapingResultDto> results
    ) {
        List<PageTextDto> parsedTexts = new ArrayList<>();
        for (ScrapingResultDto result : results) {
            try {
                PageTextDto parsedText = noticeInfo.parseText(result.getDocument());
                parsedTexts.add(parsedText);
            } catch (InternalLogicException e) {
                log.warn("Exception extracting url: {}", result.getViewUrl(), e);
            }
        }

        return parsedTexts;
    }


    private List<ComplexNoticeFormatDto> htmlParsingFromScrapingResult(
            KuisHomepageNoticeInfo kuisNoticeInfo,
            List<ScrapingResultDto> requestResults
    ) {
        List<ComplexNoticeFormatDto> noticeDtoList = new LinkedList<>();

        for (ScrapingResultDto reqResult : requestResults) {
            Document document = reqResult.getDocument();
            String viewUrl = reqResult.getViewUrl();

            RowsDto rowsDto = kuisNoticeInfo.parse(document);
            List<CommonNoticeFormatDto> importantNoticeFormatDtos = rowsDto.buildImportantRowList(viewUrl);
            List<CommonNoticeFormatDto> normalNoticeFormatDtos = rowsDto.buildNormalRowList(viewUrl);
            log.debug("[{}] 공지 개수 = {}", kuisNoticeInfo.getCategoryName(), importantNoticeFormatDtos.size() + normalNoticeFormatDtos.size());
            noticeDtoList.add(new ComplexNoticeFormatDto(importantNoticeFormatDtos, normalNoticeFormatDtos));
        }

        return noticeDtoList;
    }
}
