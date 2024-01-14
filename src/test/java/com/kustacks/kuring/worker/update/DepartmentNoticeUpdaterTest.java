package com.kustacks.kuring.worker.update;

import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.DepartmentNoticeUpdater;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;


@SpringBootTest
@TestPropertySource(properties = "spring.config.location=" +
        "classpath:/application.yml" +
        ",classpath:/application-test.yml" +
        ",classpath:/test-constants.properties")
public class DepartmentNoticeUpdaterTest {

    @MockBean
    DepartmentNoticeScraperTemplate scrapperTemplate;

    @MockBean
    FirebaseService firebaseService;

    @Autowired
    DepartmentNoticeUpdater departmentNoticeUpdater;

    @Autowired
    ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;

    @Autowired
    DepartmentNoticeRepository departmentNoticeRepository;

    @DisplayName("학과별 공지 업데이트 테스트")
    @Test
    public void department_scrap_async_test() throws InterruptedException {
        // given
        doReturn(createDepartmentNoticesFixture()).when(scrapperTemplate).scrap(any(), any());
        doNothing().when(firebaseService).sendNotificationList(anyList());

        // when
        departmentNoticeUpdater.update();
        noticeUpdaterThreadTaskExecutor.getThreadPoolExecutor().awaitTermination(3, TimeUnit.SECONDS);
        List<DepartmentNotice> notices = departmentNoticeRepository.findAll();


        // then
        assertThat(notices.size()).isEqualTo(3720);
    }

    private static List<ComplexNoticeFormatDto> createDepartmentNoticesFixture() {
        List<ComplexNoticeFormatDto> result = new ArrayList<>();
        List<CommonNoticeFormatDto> importantNoticeList = new ArrayList<>();
        List<CommonNoticeFormatDto> normalNoticeList = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            importantNoticeList.add(new CommonNoticeFormatDto(String.valueOf(i), "2021-01-01",
                    "important" + i, "2021-01-01",
                    "https://library.konkuk.ac.kr/library-guide/bulletins/important/71921",
                    true));

            normalNoticeList.add(new CommonNoticeFormatDto(String.valueOf(i), "2021-01-01",
                    "normal" + i, "2021-01-01",
                    "https://library.konkuk.ac.kr/library-guide/bulletins/normal/71921",
                    false));
        }

        result.add(new ComplexNoticeFormatDto(importantNoticeList, normalNoticeList));
        return result;
    }
}
