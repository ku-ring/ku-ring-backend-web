package com.kustacks.kuring.message.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.kustacks.kuring.admin.application.port.out.dto.AdminNotificationDto;
import com.kustacks.kuring.message.firebase.dto.NoticeMessageDto;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.firebase.exception.FirebaseMessageSendException;
import com.kustacks.kuring.message.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.firebase.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

    private static final String NOTIFICATION_TITLE = "새로운 공지가 왔어요!";
    public static final String ALL_DEVICE_SUBSCRIBED_TOPIC = "allDevice";

    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;
    private final ServerProperties serverProperties;

    public void validationToken(String token) throws FirebaseInvalidTokenException {
        try {
            Message message = Message.builder().setToken(token).build();

            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseInvalidTokenException();
        }
    }

    public void subscribe(String token, String topic) throws FirebaseSubscribeException {
        try {
            TopicManagementResponse response = firebaseMessaging
                    .subscribeToTopic(List.of(token), serverProperties.ifDevThenAddSuffix(topic));

            if (response.getFailureCount() > 0) {
                throw new FirebaseSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseSubscribeException exception) {
            throw new FirebaseSubscribeException();
        }
    }

    public void unsubscribe(String token, String topic) throws FirebaseUnSubscribeException {
        try {
            TopicManagementResponse response = firebaseMessaging
                    .unsubscribeFromTopic(List.of(token), serverProperties.ifDevThenAddSuffix(topic));

            if (response.getFailureCount() > 0) {
                throw new FirebaseUnSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseUnSubscribeException exception) {
            throw new FirebaseUnSubscribeException();
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
    public void sendNotification(NoticeMessageDto messageDto) throws FirebaseMessageSendException {
        sendBaseNotification(messageDto, serverProperties::ifDevThenAddSuffix);
    }

    public void sendTestNotification(NoticeMessageDto messageDto) throws FirebaseMessageSendException {
        sendBaseNotification(messageDto, serverProperties::addDevSuffix);
    }

    public void sendNotificationByAdmin(AdminNotificationDto messageDto) {
        try {
            Message newMessage = Message.builder()
                    .setNotification(Notification
                            .builder()
                            .setTitle(messageDto.title())
                            .setBody(messageDto.body())
                            .build())
                    .putAllData(objectMapper.convertValue(messageDto, Map.class))
                    .setTopic(serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC))
                    .build();

            firebaseMessaging.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }

    public void sendNotificationList(List<? extends Notice> noticeList) {
        List<NoticeMessageDto> notificationDtoList = createNotification(noticeList);

        try {
            this.sendNoticeMessageList(notificationDtoList);
            log.info("FCM에 {}개의 공지 메세지를 전송했습니다.", notificationDtoList.size());
            log.info("전송된 공지 목록은 다음과 같습니다.");
            for (Notice notice : noticeList) {
                log.info("아이디 = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}", notice.getArticleId(), notice.getPostedDate(), notice.getCategoryName(), notice.getSubject());
            }
        } catch (FirebaseMessageSendException e) {
            log.error("새로운 공지의 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch (Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    private void sendBaseNotification(NoticeMessageDto messageDto, Function<String, String> suffixUtil) throws FirebaseMessageSendException {
        try {
            Message newMessage = Message.builder()
                    .setNotification(Notification
                            .builder()
                            .setTitle(buildTitle(messageDto.getCategoryKorName()))
                            .setBody(messageDto.getSubject())
                            .build())
                    .putAllData(objectMapper.convertValue(messageDto, Map.class))
                    .setTopic(suffixUtil.apply(messageDto.getCategory()))
                    .build();

            firebaseMessaging.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }

    private List<NoticeMessageDto> createNotification(List<? extends Notice> willBeNotiNotices) {
        return willBeNotiNotices.stream()
                .map(NoticeMessageDto::from)
                .toList();
    }

    private void sendNoticeMessageList(List<NoticeMessageDto> messageDtoList) throws FirebaseMessageSendException {
        messageDtoList.forEach(this::sendNotification);
    }

    private String buildTitle(String korName) {
        return new StringBuilder("[")
                .append(korName)
                .append("] ")
                .append(NOTIFICATION_TITLE)
                .toString();
    }
}
