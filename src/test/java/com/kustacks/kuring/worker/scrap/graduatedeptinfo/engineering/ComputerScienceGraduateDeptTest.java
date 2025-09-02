package com.kustacks.kuring.worker.scrap.graduatedeptinfo.engineering;

import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ComputerScienceGraduateDeptTest {
    @Autowired
    private ComputerScienceGraduateDept computerScienceGraduateDept;

    @Autowired
    private DepartmentNoticeScraperTemplate scraperTemplate;

    @Test
    @DisplayName("컴퓨터공학부 대학원 공지를 스크랩한다.")
    void scrapGraduateNotices() {
        //when
        List<ComplexNoticeFormatDto> result =
                scraperTemplate.scrap(computerScienceGraduateDept, DeptInfo::scrapLatestPageHtml);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getNormalNoticeList()).isNotEmpty();
    }
}
