package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertCreateRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.TestNotificationRequest;
import com.kustacks.kuring.support.IntegrationTestSupport;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.kustacks.kuring.acceptance.AdminStep.금칙어_로드_요청;
import static com.kustacks.kuring.acceptance.AdminStep.사용자_피드백_조회_요청;
import static com.kustacks.kuring.acceptance.AdminStep.신고_목록_조회_요청;
import static com.kustacks.kuring.acceptance.AdminStep.신고_목록_조회_확인;
import static com.kustacks.kuring.acceptance.AdminStep.알림_예약;
import static com.kustacks.kuring.acceptance.AdminStep.예약_알림_삭제;
import static com.kustacks.kuring.acceptance.AdminStep.예약_알림_조회;
import static com.kustacks.kuring.acceptance.AdminStep.피드백_조회_확인;
import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 관리자")
class AdminAcceptanceTest extends IntegrationTestSupport {

    AdminAlertCreateRequest alertCreateCommand;

    @Autowired
    Clock clock;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        LocalDateTime expiredTime = LocalDateTime.now(clock).plus(1, ChronoUnit.HOURS);
        alertCreateCommand = new AdminAlertCreateRequest(
                "title", "content", expiredTime.toString()
        );
    }

    /**
     * given : 사전에 등록된 어드민가 피드백들이 이다
     * when : 어드민이 피드백 조회시
     * then : 성공적으로 조회된다
     */
    @DisplayName("[v2] 사용자 피드백 조회")
    @Test
    void role_root_admin_search_feedbacks() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var 사용자_피드백 = 사용자_피드백_조회_요청(accessToken);

        // then
        피드백_조회_확인(사용자_피드백);
    }

    /**
     * given : 사전에 등록된 어드민가 피드백들이 이다
     * when : 어드민이 피드백 조회시
     * then : 성공적으로 조회된다
     */
    @DisplayName("[v2] 신고 목록 조회")
    @Test
    void role_root_admin_search_reports() {
        댓글_작성_및_신고();
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var 신고_목록 = 신고_목록_조회_요청(accessToken);

        // then
        신고_목록_조회_확인(신고_목록);
    }

    /**
     * given : 사전에 등록된 어드민이 있다
     * when : 테스트 공지를 발송하면
     * then : 성공적으로 발송된다.
     */
    @DisplayName("[v2] 테스트 공지 발송")
    @Test
    void role_root_admin_create_test_notification() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new TestNotificationRequest("bachelor", "테스트 공지입니다", "1234"))
                .when().post("/api/v2/admin/notices/dev")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("테스트 공지 생성에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }

    /**
     * given : 사전에 등록된 어드민이 있다
     * when : 실제 공지를 발송하면
     * then : 성공적으로 발송된다.
     */
    @DisplayName("[v2] 실제 공지 발송")
    @Test
    void role_root_admin_create_real_notification() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new RealNotificationRequest("real 공지", "real 공지입니다", "https://www.naver.com", ADMIN_PASSWORD))
                .when().post("/api/v2/admin/notices/prod")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("실제 공지 생성에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }

    /**
     * Given : 등록된 ROLE_ROOT의 Admin이 있다.
     * When : 원하는 시간에 예약 알림을 등록한다
     * Then : 성공적으로 등록된다.
     */
    @DisplayName("[v2] Admin은 예약 알림을 생성할 수 있다")
    @Test
    void add_alert_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var 알림_예약_응답 = 알림_예약(accessToken, alertCreateCommand);

        // then
        assertAll(
                () -> assertThat(알림_예약_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(알림_예약_응답.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(알림_예약_응답.jsonPath().getString("message")).isEqualTo("실제 공지 생성에 성공하였습니다"),
                () -> assertThat(알림_예약_응답.jsonPath().getString("data")).isNull()
        );
    }

    /**
     * Given : Admin이 등록한 예약 알림이 있다
     * When : 예약 알림을 조회한다
     * Then : 예약되어 있던 모든 알림을 조회한다
     */
    @DisplayName("[v2] Admin은 예약된 모든 알림을 조회할 수 있다")
    @Test
    void lookup_all_alert_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);
        알림_예약(accessToken, alertCreateCommand);

        // when
        var response = 예약_알림_조회(accessToken);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("예약 알림 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).contains("id", "title", "content", "status", "wakeTime")
        );
    }

    /**
     * Given : Admin이 등록한 예약 알림이 있다
     * When : 특정 예약 알림을 삭제한다
     * Then : 성공적으로 삭제된다.
     */
    @DisplayName("[v2] Admin은 예약 알림을 삭제할 수 있다")
    @Test
    void delete_alert_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);
        알림_예약(accessToken, alertCreateCommand);
        int alertId = 예약_알림_조회(accessToken).jsonPath().getInt("data[0].id");

        // when
        var 예약_알림_삭제_응답 = 예약_알림_삭제(accessToken, alertId);

        // then
        assertThat(예약_알림_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        var response = 예약_알림_조회(accessToken);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("예약 알림 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data[0].status")).isEqualTo("CANCELED")
        );
    }

    @DisplayName("[v2] Admin은 금칙어 수동 로드할 수 있다.")
    @Test
    void admin_can_load_bad_words() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var 금칙어_로드_응답 = 금칙어_로드_요청(accessToken);

        // then
        AdminStep.금칙어_로드_응답_확인(금칙어_로드_응답);
    }

    /**
     * Given : 등록된 ROLE_ROOT의 Admin이 있다.
     * When : ROLE_ROOT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Root -> Root API 접근 테스트")
    @Test
    void role_root_admin_call_root_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/root")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data")).contains("ROLE_CLIENT", "ROLE_ROOT")
        );
    }

    /**
     * Given : 등록된 ROLE_ROOT의 Admin이 있다.
     * When : ROLE_CLIENT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Root -> Client API 접근 테스트")
    @Test
    void role_root_admin_call_client_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/client")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).contains("ROLE_CLIENT", "ROLE_ROOT")
        );
    }

    /**
     * Given : 등록된 ROLE_CLIENT의 Admin이 있다.
     * When : ROLE_CLIENT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Client -> Client API 접근 테스트")
    @Test
    void role_client_admin_call_client_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_CLIENT_LOGIN_ID, ADMIN_CLIENT_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/client")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).contains("ROLE_CLIENT")
        );
    }

    /**
     * Given : 등록된 ROLE_CLIENT의 Admin이 있다.
     * When : ROLE_ROOT의 API에 접근시
     * Then : 응답받을 수 없다
     */
    @DisplayName("[v2] Client -> Root API 접근 테스트")
    @Test
    void role_client_admin_call_root_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_CLIENT_LOGIN_ID, ADMIN_CLIENT_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/root")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(401),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 실패하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }

    private void 댓글_작성_및_신고() {
        String userAccessToken = UserStep.사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        NoticeStep.공지에_댓글_추가(1L, userAccessToken, "댓글 내용 1");
        NoticeStep.공지에_댓글_추가(1L, userAccessToken, "댓글 내용 2");
        NoticeStep.공지에_댓글_추가(1L, userAccessToken, "댓글 내용 3");

        ReportStep.신고_요청(USER_FCM_TOKEN, 1L, "comment", "댓글 신고 내용 1");
        ReportStep.신고_요청(USER_FCM_TOKEN, 2L, "comment", "댓글 신고 내용 2");
        ReportStep.신고_요청(USER_FCM_TOKEN, 3L, "comment", "댓글 신고 내용 3");
    }
}
