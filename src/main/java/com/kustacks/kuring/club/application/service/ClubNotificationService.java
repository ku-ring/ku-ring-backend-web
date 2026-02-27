package com.kustacks.kuring.club.application.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kustacks.kuring.club.application.port.in.ClubNotificationUseCase;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.kustacks.kuring.message.domain.MessageType.CLUB;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class ClubNotificationService implements ClubNotificationUseCase {

    private static final String CLUB_TOPIC_PREFIX = "club.";
    private static final String D_DAY_1_TITLE = "[D-1] %s 동아리 모집";
    private static final String D_DAY_1_BODY = "내일 마감되기 전에 지원하세요!";

    private final ClubQueryPort clubQueryPort;
    private final FirebaseMessagingPort firebaseMessagingPort;
    private final ServerProperties serverProperties;

    @Override
    public void sendDeadlineNotifications() {
        List<Club> clubs = findDeadlineClubs();
        clubs.forEach(this::sendDeadLineClubNotification);
    }

    private void sendDeadLineClubNotification(Club club) {
        try {
            Message message = buildMessage(club);
            firebaseMessagingPort.send(message);
            log.info("동아리 마감 알림 발송 완료. clubId={}", club.getId());
        } catch (Exception e) {
            log.error("동아리 마감 알림 발송 실패. clubId={}", club.getId(), e);
        }
    }

    private List<Club> findDeadlineClubs() {
        return clubQueryPort.findNextDayRecruitEndClubs(LocalDateTime.now());
    }

    private Message buildMessage(Club club) {
        String messageTitle = String.format(D_DAY_1_TITLE, club.getName());

        return Message.builder()
                .setNotification(buildNotification(messageTitle, D_DAY_1_BODY))
                .setTopic(buildTopic(club))
                .putAllData(buildMessageData(messageTitle, D_DAY_1_BODY, club))
                .build();
    }

    private Map<String, String> buildMessageData(String title, String body, Club club) {
        return Map.of(
                "clubId", String.valueOf(club.getId()),
                "title", title,
                "body", body,
                "messageType", CLUB.getValue()
        );
    }

    private String buildTopic(Club club) {
        return serverProperties.ifDevThenAddSuffix(CLUB_TOPIC_PREFIX + club.getId());
    }

    private Notification buildNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
