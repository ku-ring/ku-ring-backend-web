package com.kustacks.kuring.admin.application.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.admin.application.port.out.AdminAlertEventPort;
import com.kustacks.kuring.admin.application.port.out.AdminEventPort;
import com.kustacks.kuring.admin.application.port.out.AdminUserFeedbackPort;
import com.kustacks.kuring.admin.application.port.out.AiEventPort;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.application.port.in.dto.DataEmbeddingCommand;
import com.kustacks.kuring.auth.userdetails.UserDetailsServicePort;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.notice.domain.CategoryName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final AdminAlertEventPort adminAlertEventPort;
    private final AdminEventPort adminEventPort;
    private final AiEventPort aiEventPort;
    private final NoticeProperties noticeProperties;
    private final ServerProperties serverProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createTestNotice(TestNotificationCommand command) {
        String testNoticePostedDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        CategoryName testCategoryName = CategoryName.fromStringName(command.category());

        adminEventPort.sendTestNotificationByAdmin(
                command.articleId(),
                testNoticePostedDate,
                testCategoryName.getName(),
                command.subject(),
                testCategoryName.getKorName(),
                CategoryName.LIBRARY.equals(testCategoryName)
                        ? noticeProperties.libraryBaseUrl()
                        : noticeProperties.normalBaseUrl()
        );
    }

    @Transactional
    @Override
    public void createRealNoticeForAllUser(RealNotificationCommand command) {
        Admin admin = (Admin) userDetailsServicePort
                .loadUserByUsername(command.getStringPrincipal());

        if (isNotMatchPassword(command.adminPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("관리자 비밀번호가 일치하지 않습니다.");
        }

        adminEventPort.sendNotificationByAdmin(command.title(), command.body(), command.url());
    }

    @Override
    public void addAlertSchedule(AlertCreateCommand command) {
        adminAlertEventPort.addAlertSchedule(command.title(), command.content(), command.alertTime());
    }

    @Override
    public void cancelAlertSchedule(Long id) {
        adminAlertEventPort.cancelAlertSchedule(id);
    }

    @Override
    public void embeddingCustomData(DataEmbeddingCommand command) {
        try {
            MultipartFile file = command.file();

            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            Resource resource = new InputStreamResource(file.getInputStream());
            String extension = extractExtension(originalFilename);

            aiEventPort.sendDataEmbeddingEvent(
                    originalFilename,
                    extension,
                    contentType,
                    resource
            );
        } catch (IOException e) {
            log.error("file read error", e);
        }
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

    private boolean isNotMatchPassword(final String commandPassword, final String adminPassword) {
        return !passwordEncoder.matches(commandPassword, adminPassword);
    }

    private String extractExtension(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
