package com.kustacks.kuring.admin.application.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.admin.application.port.out.AdminEventPort;
import com.kustacks.kuring.admin.application.port.out.AdminUserFeedbackPort;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.auth.userdetails.UserDetailsServicePort;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.notice.domain.CategoryName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AdminCommandService implements AdminCommandUseCase {

    private final UserDetailsServicePort userDetailsServicePort;
    private final AdminUserFeedbackPort adminUserFeedbackPort;
    private final AdminEventPort adminEventPort;
    private final NoticeProperties noticeProperties;
    private final ServerProperties serverProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createTestNotice(TestNotificationCommand command) {
        String testNoticePostedDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        CategoryName testCategoryName = CategoryName.fromStringName(command.category());

        adminEventPort.sendTestNotificationByAdmin(
                command.articleId(),
                testNoticePostedDate,
                testCategoryName.getName(),
                command.subject(),
                testCategoryName.getKorName(),
                CategoryName.LIBRARY.equals(testCategoryName)
                        ? noticeProperties.getLibraryBaseUrl()
                        : noticeProperties.getNormalBaseUrl()
        );
    }

    @Transactional
    @Override
    public void createRealNoticeForAllUser(RealNotificationCommand command) {
        Admin admin = (Admin) userDetailsServicePort
                .loadUserByUsername(command.getStringPrincipal());

        if (!passwordEncoder.matches(command.adminPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("관리자 비밀번호가 일치하지 않습니다.");
        }

        adminEventPort.sendNotificationByAdmin(command.title(), command.body(), command.url());
    }

    /**
     * TODO : 1회성 API - client v2 배포 후, 단 한번 모든 사용자를 공통 topic에 구독시킨 후 제거 예정
     */
    @Transactional
    @Override
    public void subscribeAllUserSameTopic() {
        String topic = serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC);

        FirebaseMessaging instance = FirebaseMessaging.getInstance();
        List<String> allToken = adminUserFeedbackPort.findAllToken();

        int size = allToken.size();
        for (int i = 0; i < size; i += 500) {
            List<String> subList = allToken.subList(i, Math.min(i + 500, size));
            instance.subscribeToTopicAsync(subList, topic);
        }
    }
}
