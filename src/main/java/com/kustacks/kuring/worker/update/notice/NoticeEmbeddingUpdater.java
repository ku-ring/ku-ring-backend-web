package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.ai.application.port.out.CommandVectorStorePort;
import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import com.kustacks.kuring.worker.scrap.KuisHomepageNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeEmbeddingUpdater {

    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;
    private final KuisHomepageNoticeScraperTemplate scrapperTemplate;
    private final List<KuisHomepageNoticeInfo> kuisNoticeInfoList;
    private final CommandVectorStorePort commandVectorStorePort;
    private final NoticeCommandPort noticeCommandPort;
    private final NoticeQueryPort noticeQueryPort;
    private final FeatureFlags featureFlags;

    /*
    학사, 장학, 취창업, 국제, 학생, 산학, 일반, 공지 embedding
    */
    @Scheduled(cron = "0 5/20 7-19 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 7:05 ~ 오후 7:55분 사이에 20분마다 업데이트 된다.
    public void update() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_NOTICE_EMBEDDING.getFeature())) {

            log.info("========== KUIS Hompage Embedding 시작 ==========");

            for (KuisHomepageNoticeInfo kuisNoticeInfo : kuisNoticeInfoList) {
                CompletableFuture
                        .supplyAsync(
                                () -> lookupNotYetEmbeddingNotice(kuisNoticeInfo),
                                noticeUpdaterThreadTaskExecutor
                        ).thenApply(
                                scrapResults -> scrapNoticeText(scrapResults, kuisNoticeInfo)
                        ).thenAccept(
                                scrapResults -> embeddingNotice(scrapResults, kuisNoticeInfo.getCategoryName())
                        );
            }
        }
    }

    private List<NoticeDto> lookupNotYetEmbeddingNotice(KuisHomepageNoticeInfo noticeInfo) {
        log.debug("lookupNotYetEmbeddingNotice {}", noticeInfo.getCategoryName());
        LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
        return noticeQueryPort.findNotYetEmbeddingNotice(noticeInfo.getCategoryName(), startDate);
    }

    private List<PageTextDto> scrapNoticeText(
            List<NoticeDto> scrapResults,
            KuisHomepageNoticeInfo noticeInfo
    ) {
        return scrapperTemplate.scrapForEmbedding(scrapResults, noticeInfo);
    }

    private void embeddingNotice(List<PageTextDto> extractTextResults, CategoryName categoryName) {
        if (extractTextResults.isEmpty()) {
            log.debug("Embedding {} no more notice to embed", categoryName);
            return;
        }
        log.info("Embedding {}, size = {}", categoryName, extractTextResults.size());

        commandVectorStorePort.embedding(extractTextResults, categoryName);
        List<String> articleIds = extractTextResults.stream()
                .map(PageTextDto::articleId)
                .toList();

        noticeCommandPort.updateNoticeEmbeddingStatus(categoryName, articleIds);
    }
}
