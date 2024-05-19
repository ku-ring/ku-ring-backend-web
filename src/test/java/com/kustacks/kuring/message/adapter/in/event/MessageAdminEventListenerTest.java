package com.kustacks.kuring.message.adapter.in.event;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.admin.application.service.AdminCommandService;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.kustacks.kuring.admin.domain.AdminRole.ROLE_ROOT;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("[단위] 어드민 이벤트 리스너 테스트")
class MessageAdminEventListenerTest extends IntegrationTestSupport {

    @Autowired
    private AdminCommandService adminCommandService;

    @MockBean
    private MessageAdminEventListener messageAdminEventListener;

    @DisplayName("어드민이 커스텀으로 생성한 알림을 전송할 수 있다")
    @Test
    void sendNotificationEvent() {
        // given
        RealNotificationCommand command = new RealNotificationCommand(
                "test title",
                "test body",
                "test url",
                ADMIN_PASSWORD,
                new Authentication(ADMIN_LOGIN_ID, List.of(ROLE_ROOT.name()))
        );

        // when
        adminCommandService.createRealNoticeForAllUser(command);

        // then
        Mockito.verify(messageAdminEventListener).sendNotificationEvent(any());
    }

    @DisplayName("어드민이 커스텀으로 생성한 테스트 알림을 전송할 수 있다")
    @Test
    void sendTestNotificationEvent() {
        // given
        TestNotificationCommand command = new TestNotificationCommand(
                CategoryName.STUDENT.getName(),
                "test body",
                "test url"
        );

        // when
        adminCommandService.createTestNotice(command);

        // then
        Mockito.verify(messageAdminEventListener).sendTestNotificationEvent(any());
    }
}
