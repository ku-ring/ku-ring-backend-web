package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.support.MockServerSupport;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.engineering.ComputerScienceDept;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("교직원 스크랩 테스트")
@SpringBootTest
public class StaffScraperTest {

    @Autowired
    private StaffScraper staffScraper;

    @Autowired
    private ComputerScienceDept computerScienceDept;

    @Autowired
    private RealEstateDept realEstateDept;

    private ClientAndServer mockServer;
    private final int MOCK_SERVER_PORT = 9000;

    @Value("${staff.each-dept-url}")
    private String eachDeptUrl;

    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT); // 원하는 포트 지정
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }

    @DisplayName("컴퓨터공학부 교직원 정보를 스크랩한다.")
    @Test
    void cseStaffScrapTest() throws IOException {
        // given
        setUpMockServerWith200Response(buildDeptStaffPageUrl(computerScienceDept.getStaffSiteName(), String.valueOf(computerScienceDept.getStaffSiteIds().get(0))),
                TestFileLoader.loadHtmlFile("src/test/resources/staff/cse_staff_page.html")
        );

        // when
        List<StaffDto> scrapResult = staffScraper.scrap(computerScienceDept);

        // then
        assertAll(
                () -> assertThat(scrapResult).hasSize(23),
                () -> assertThat(scrapResult.get(0).getName()).isEqualTo("김기천 (Keecheon Kim)"),
                () -> assertThat(scrapResult.get(0).getPosition()).isEqualTo("교수"),
                () -> assertThat(scrapResult.get(0).getMajor()).isEqualTo("Computer Communications and Mobile Computing"),
                () -> assertThat(scrapResult.get(0).getLab()).isEqualTo("공C385-2"),
                () -> assertThat(scrapResult.get(0).getPhone()).isEqualTo("02-450-3518"),
                () -> assertThat(scrapResult.get(0).getEmail()).isEqualTo("kckim@konkuk.ac.kr")

        );
    }

    @DisplayName("부동산학과 교직원 정보를 스크랩한다.")
    @Test
    void realEstateStaffScrapTest() throws IOException {
        // given
        setUpMockServerWith200Response(buildDeptStaffPageUrl(realEstateDept.getStaffSiteName(), String.valueOf(realEstateDept.getStaffSiteIds().get(0))),
                TestFileLoader.loadHtmlFile("src/test/resources/staff/cse_staff_page.html")
        );

        // when
        List<StaffDto> scrapResult = staffScraper.scrap(realEstateDept);

        // then
        assertAll(
                () -> assertThat(scrapResult).hasSize(11),
                () -> assertThat(scrapResult.get(0).getName()).isEqualTo("정의철"),
                () -> assertThat(scrapResult.get(0).getPosition()).isEqualTo("교수"),
                () -> assertThat(scrapResult.get(0).getMajor()).isEqualTo("부동산경제학, 부동산정책론"),
                () -> assertThat(scrapResult.get(0).getLab()).isEqualTo("해봉부동산학관 702호"),
                () -> assertThat(scrapResult.get(0).getPhone()).isEqualTo("02-450-4069"),
                () -> assertThat(scrapResult.get(0).getEmail()).isEqualTo("echung@konkuk.ac.kr")
        );
    }

    private String buildDeptStaffPageUrl(String siteName, String siteId) {
        return eachDeptUrl
                .replaceAll("\\{department\\}", siteName)
                .replace("{siteId}", siteId);
    }

    private void setUpMockServerWith200Response(String requestPath, String responseBody) {
        HttpRequest request = MockServerSupport.getMockRequest("GET", requestPath);
        HttpResponse response = MockServerSupport.getMockResponse(200, MediaType.TEXT_HTML_UTF_8, responseBody);
        MockServerSupport.createNewMockServer(MOCK_SERVER_PORT, request, response);
    }
}
