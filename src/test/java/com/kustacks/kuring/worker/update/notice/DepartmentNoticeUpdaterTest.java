package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.message.application.service.FirebaseNotificationService;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;


@SpringBootTest
class DepartmentNoticeUpdaterTest {

    @MockBean
    DepartmentNoticeScraperTemplate scrapperTemplate;

    @MockBean
    FirebaseNotificationService firebaseService;

    @Autowired
    DepartmentNoticeUpdater departmentNoticeUpdater;

    @Autowired
    ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;

    @Autowired
    NoticeQueryPort noticeQueryPort;

    @DisplayName("학과별 공지 업데이트 테스트")
    @Test
    void department_scrap_async_test() throws InterruptedException {
        // given
        doReturn(createDepartmentNoticesFixture()).when(scrapperTemplate).scrap(any(), any());
        doNothing().when(firebaseService).sendNotificationList(anyList());

        // when
        departmentNoticeUpdater.update();
        noticeUpdaterThreadTaskExecutor.getThreadPoolExecutor().awaitTermination(2, TimeUnit.SECONDS);

        // then
        Long count = noticeQueryPort.count();
        assertThat(count).isEqualTo(3720);
    }

    private static List<ComplexNoticeFormatDto> createDepartmentNoticesFixture() {
        List<ComplexNoticeFormatDto> result = new ArrayList<>();
        List<CommonNoticeFormatDto> importantNoticeList = new ArrayList<>();
        List<CommonNoticeFormatDto> normalNoticeList = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            CommonNoticeFormatDto importantFormatDto = CommonNoticeFormatDto.builder().articleId(String.valueOf(i)).updatedDate("2021-01-01").subject("important" + i)
                    .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/important/71921")
                    .important(true).build();
            importantNoticeList.add(importantFormatDto);

            CommonNoticeFormatDto normalFormatDto = CommonNoticeFormatDto.builder().articleId(String.valueOf(i)).updatedDate("2021-01-01").subject("normal" + i)
                    .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921")
                    .important(false).build();
            normalNoticeList.add(normalFormatDto);
        }

        result.add(new ComplexNoticeFormatDto(importantNoticeList, normalNoticeList));
        return result;
    }
}
