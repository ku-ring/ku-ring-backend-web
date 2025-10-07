package com.kustacks.kuring.admin.application.service;

import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.admin.application.port.out.AdminAlertEventPort;
import com.kustacks.kuring.admin.application.port.out.AdminEventPort;
import com.kustacks.kuring.admin.application.port.out.AiEventPort;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.application.port.in.dto.DataEmbeddingCommand;
import com.kustacks.kuring.auth.userdetails.UserDetailsServicePort;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ACADEMIC_EVENT_TOPIC;
import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AdminCommandService implements AdminCommandUseCase {

    private final UserDetailsServicePort userDetailsServicePort;
    private final AdminAlertEventPort adminAlertEventPort;
    private final AdminEventPort adminEventPort;
    private final AiEventPort aiEventPort;
    private final UserQueryPort userQueryPort;
    private final FirebaseSubscribePort firebaseSubscribePort;
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

    @Override
    public void resubscribeAllUsersToTopics() {
        List<User> allUsers = userQueryPort.findAllWithSubscriptions();
        Map<String, List<String>> topicSubscriptions = new HashMap<>();

        //User 당 (1 + 학사일정 알림 구독여부 + 유저별 카테고리 구독 수 + 유저별 학과 구독 수) 반복
        //User 당 최대 2 + 카테고리 수 + 학과 수 => 넉넉 잡아 80
        //ex. User 5000명 * 80 => 400,000번 반복
        for (User user : allUsers) {
            String fcmToken = user.getFcmToken();

            // allDevice 토픽 - 모든 사용자
            String allDeviceTopic = serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC);
            topicSubscriptions.computeIfAbsent(allDeviceTopic, k -> new LinkedList<>()).add(fcmToken);

            // academicEvent 토픽 - 학사일정 알림이 활성화된 사용자
            if (user.isAcademicEventNotificationEnabled()) {
                String academicEventTopic = serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC);
                topicSubscriptions.computeIfAbsent(academicEventTopic, k -> new LinkedList<>()).add(fcmToken);
            }

            // 카테고리별 토픽
            user.getSubscribedCategoryList().forEach(category -> {
                String categoryTopic = serverProperties.ifDevThenAddSuffix(category.getName());
                topicSubscriptions.computeIfAbsent(categoryTopic, k -> new LinkedList<>()).add(fcmToken);
            });

            // 학과별 토픽
            user.getSubscribedDepartmentList().forEach(department -> {
                String departmentTopic = serverProperties.ifDevThenAddSuffix(department.getName());
                topicSubscriptions.computeIfAbsent(departmentTopic, k -> new LinkedList<>()).add(fcmToken);
            });
        }

        // 토픽별로 500개씩 나누어 구독 처리
        for (Map.Entry<String, List<String>> entry : topicSubscriptions.entrySet()) {
            String topic = entry.getKey();
            List<String> tokens = entry.getValue();

            log.info("Resubscribing {} users to topic: {}", tokens.size(), topic);

            int successCount = 0;
            int failureCount = 0;

            for (int i = 0; i < tokens.size(); i += 500) {
                List<String> batch = tokens.subList(i, Math.min(i + 500, tokens.size()));
                try {
                    firebaseSubscribePort.subscribeToTopic(batch, topic);
                    successCount += batch.size();
                } catch (Exception e) {
                    failureCount += batch.size();
                }
            }

            log.info("Resubscribed {} users to topic: {}. {} users failed.", successCount, topic, failureCount);
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
