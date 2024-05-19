package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class KuisNoticeScraperTemplate {

    public List<CommonNoticeFormatDto> scrap(KuisNoticeInfo kuisNoticeRequestBody, Function<KuisNoticeInfo, List<CommonNoticeFormatDto>> decisionMaker) throws InternalLogicException {
        List<CommonNoticeFormatDto> requestResults = requestWithKuisInfo(kuisNoticeRequestBody, decisionMaker);

        if(requestResults == null || requestResults.isEmpty()) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP);
        }

        return requestResults;
    }

    private List<CommonNoticeFormatDto> requestWithKuisInfo(KuisNoticeInfo kuisNoticeRequestBody, Function<KuisNoticeInfo, List<CommonNoticeFormatDto>> decisionMaker) {
        long startTime = System.currentTimeMillis();
        List<CommonNoticeFormatDto> requestResults = decisionMaker.apply(kuisNoticeRequestBody);
        long endTime = System.currentTimeMillis();

        log.debug("[{}] 파싱에 소요된 초 = {}", kuisNoticeRequestBody.getCategoryName().getName(), (endTime - startTime) / 1000.0);
        return requestResults;
    }
}
