package com.kustacks.kuring.message.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.FirebaseWithAdminUseCase;
import com.kustacks.kuring.message.application.port.in.dto.AcademicTestNotificationCommand;
import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;
import com.kustacks.kuring.message.application.port.in.dto.AdminTestNotificationCommand;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.dto.NoticeMessageDto;
import com.kustacks.kuring.message.application.service.exception.FirebaseMessageSendException;
import com.kustacks.kuring.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ACADEMIC_EVENT_TOPIC;
import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;
import static com.kustacks.kuring.message.domain.MessageType.ACADEMIC;
import static com.kustacks.kuring.message.domain.MessageType.ADMIN;
import static com.kustacks.kuring.message.domain.MessageType.NOTICE;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class FirebaseNotificationService implements FirebaseWithAdminUseCase {

    private static final String NOTIFICATION_TITLE = "새로운 공지가 왔어요!";
    private static final String NO_NEW_NOTICE = "새로운 공지가 없습니다.";

    private final FirebaseMessagingPort firebaseMessagingPort;
    private final ServerProperties serverProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void sendTestNotificationByAdmin(AdminTestNotificationCommand command) {
        NoticeMessageDto messageDto = convertDtoFromCommand(command);
        sendBaseNotification(messageDto, serverProperties::addDevSuffix);
    }

    @Override
    public void sendNotificationByAdmin(AdminNotificationCommand command) {
        try {
            Message newMessage = createMessageFromCommand(command);
            firebaseMessagingPort.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }

    public void sendNotifications(List<? extends Notice> noticeList) {
        if (noticeList.isEmpty()) {
            log.info(NO_NEW_NOTICE);
            return;
        }

        List<NoticeMessageDto> notificationDtoList = createNotification(noticeList);
        sendNotices(notificationDtoList);
    }

    @Override
    public void sendAcademicTestNotification(AcademicTestNotificationCommand command) {
        try {
            Message newMessage = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(command.title())
                            .setBody(command.body())
                            .build())
                    .putAllData(Map.of(
                            "title", command.title(),
                            "body", command.body(),
                            "messageType", ACADEMIC.getValue()
                    ))
                    .setTopic(serverProperties.addDevSuffix(ACADEMIC_EVENT_TOPIC))
                    .build();

            firebaseMessagingPort.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }


    private void sendNotices(List<NoticeMessageDto> notificationDtoList) {
        try {
            loggingNoticeSendInfo(notificationDtoList);
            this.sendNoticeMessages(notificationDtoList);
        } catch (FirebaseMessageSendException e) {
            log.error("새로운 공지의 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch (Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    private void loggingNoticeSendInfo(List<NoticeMessageDto> notificationDtoList) {
        log.info("FCM에 {}카테고리에 {}개의 공지 메세지를 전송.",
                notificationDtoList.get(0).getCategory(), notificationDtoList.size());

        log.info("전송된 공지 목록은 다음과 같습니다.");
        for (NoticeMessageDto noticeMessageDto : notificationDtoList) {
            log.info("ID = {}, ArticleId = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}",
                    noticeMessageDto.getId(),
                    noticeMessageDto.getArticleId(),
                    noticeMessageDto.getPostedDate(),
                    noticeMessageDto.getCategory(),
                    noticeMessageDto.getSubject()
            );
        }
    }

    /**
     * Firebase message에는 두 가지 paylaad가 존재한다.
     * 1. notification
     * 2. data
     * <p>
     * notification을 Message로 만들어 보내면 여기서 설정한 title, body가 직접 앱 noti로 뜬다.
     * data로 Message를 만들어 보내면 이것을 앱 클라이언트(Andriod)가 받아서, 가공한 뒤 푸쉬 알람으로 만들 수 있다.
     * <p>
     * 따라서 여기선 putData를 사용하여 보내고, 클라이언트가 푸쉬 알람을 만들어 띄운다.
     *
     * @param messageDto
     * @throws FirebaseMessageSendException
     */
    private void sendNotification(NoticeMessageDto messageDto) throws FirebaseMessageSendException {
        sendBaseNotification(messageDto, serverProperties::ifDevThenAddSuffix);
    }

    private void sendBaseNotification(
            NoticeMessageDto messageDto,
            UnaryOperator<String> suffixUtil
    ) throws FirebaseMessageSendException {
        try {
            Message newMessage = createMessageFromDto(messageDto, suffixUtil);
            firebaseMessagingPort.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }

    private Message createMessageFromCommand(AdminNotificationCommand command) {
        return Message.builder()
                .setNotification(Notification
                        .builder()
                        .setTitle(command.title())
                        .setBody(command.body())
                        .build())
                .putAllData(objectMapper.convertValue(command, Map.class))
                .putData("messageType", ADMIN.getValue())
                .setTopic(serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC))
                .build();
    }

    private Message createMessageFromDto(NoticeMessageDto messageDto, UnaryOperator<String> suffixUtil) {
        return Message.builder()
                .setNotification(Notification
                        .builder()
                        .setTitle(buildTitle(messageDto.getCategoryKorName()))
                        .setBody(messageDto.getSubject())
                        .build())
                .putAllData(objectMapper.convertValue(messageDto, Map.class))
                .putData("messageType", NOTICE.getValue())
                .setTopic(suffixUtil.apply(messageDto.getCategory()))
                .build();
    }

    private List<NoticeMessageDto> createNotification(List<? extends Notice> willBeNotiNotices) {
        return willBeNotiNotices.stream()
                .map(NoticeMessageDto::from)
                .toList();
    }

    private void sendNoticeMessages(List<NoticeMessageDto> messageDtoList) throws FirebaseMessageSendException {
        messageDtoList.forEach(this::sendNotification);
    }

    private String buildTitle(String korName) {
        return new StringBuilder("[")
                .append(korName)
                .append("] ")
                .append(NOTIFICATION_TITLE)
                .toString();
    }

    private static NoticeMessageDto convertDtoFromCommand(AdminTestNotificationCommand command) {
        return NoticeMessageDto.builder()
                .articleId(command.articleId())
                .postedDate(command.postedDate())
                .category(command.categoryName())
                .subject(command.subject())
                .categoryKorName(command.korName())
                .baseUrl(command.url())
                .build();
    }
}
