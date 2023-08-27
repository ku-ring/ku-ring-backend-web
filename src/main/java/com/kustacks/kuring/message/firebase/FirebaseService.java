package com.kustacks.kuring.message.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.common.dto.AdminMessageDto;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.message.firebase.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.firebase.exception.FirebaseMessageSendException;
import com.kustacks.kuring.message.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.firebase.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

    private static final String NOTIFICATION_TITLE = "새로운 공지가 왔어요!";
    private static final String DEV_SUFFIX = "dev";
    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;

    @Value("${server.deploy.environment}")
    private String deployEnv;

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
                    .subscribeToTopic(List.of(token), ifDevThenAddSuffix(topic).toString());

            responseLoggingAndException(response, "subscribe");
        } catch (FirebaseMessagingException | FirebaseSubscribeException exception) {
            throw new FirebaseSubscribeException();
        }
    }

    public void unsubscribe(String token, String topic) throws FirebaseUnSubscribeException {
        try {
            TopicManagementResponse response = firebaseMessaging
                    .unsubscribeFromTopic(List.of(token), ifDevThenAddSuffix(topic).toString());

            responseLoggingAndException(response, "unsubscribe");
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
    public void sendMessage(NoticeMessageDto messageDto) throws FirebaseMessageSendException {
        try {
            Message newMessage = Message.builder()
                    .setNotification(Notification
                            .builder()
                            .setTitle(buildTitle(messageDto.getCategoryKorName()))
                            .setBody(messageDto.getSubject())
                            .build())
                    .putAllData(objectMapper.convertValue(messageDto, Map.class))
                    .setTopic(ifDevThenAddSuffix(messageDto.getCategory()).toString())
                    .build();

            firebaseMessaging.send(newMessage);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseMessageSendException();
        }
    }

    public void sendNoticeMessageForAdmin(String token, NoticeMessageDto messageDto) throws FirebaseMessagingException {
        firebaseMessaging.send(buildMessage(token, messageDto));
    }

    public void sendNoticeMessageForAdmin(String token, AdminMessageDto messageDto) throws FirebaseMessagingException {
        firebaseMessaging.send(buildMessage(token, messageDto));
    }

    public void sendNotificationByFcm(List<? extends Notice> noticeList) {
        List<NoticeMessageDto> notificationDtoList = createNotification(noticeList);

        try {
            this.sendNoticeMessageList(notificationDtoList);
            log.info("FCM에 {}개의 공지 메세지를 전송했습니다.", notificationDtoList.size());
            log.debug("전송된 공지 목록은 다음과 같습니다.");
            for (Notice notice : noticeList) {
                log.debug("아이디 = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}", notice.getArticleId(), notice.getPostedDate(), notice.getCategoryName(), notice.getSubject());
            }
        } catch (FirebaseMessageSendException e) {
            log.warn("새로운 공지의 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch (Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    private List<NoticeMessageDto> createNotification(List<? extends Notice> willBeNotiNotices) {
        return willBeNotiNotices.stream()
                .map(NoticeMessageDto::from)
                .collect(Collectors.toList());
    }

    private void sendNoticeMessageList(List<NoticeMessageDto> messageDtoList) throws FirebaseMessageSendException {
        messageDtoList.forEach(this::sendMessage);
    }

    private static void responseLoggingAndException(TopicManagementResponse response, String methodName) throws FirebaseSubscribeException {
        if (response.getFailureCount() > 0) {
            log.warn("[{}] 구독 실패", methodName);
            throw new FirebaseSubscribeException();
        }
    }

    private String buildTitle(String korName) {
        return new StringBuilder("[")
                .append(korName)
                .append("] ")
                .append(NOTIFICATION_TITLE)
                .toString();
    }

    private <T> Message buildMessage(String token, T messageDto) {
        return Message.builder()
                .putAllData(objectMapper.convertValue(messageDto, Map.class))
                .setToken(token)
                .build();
    }

    private StringBuilder ifDevThenAddSuffix(String topic) {
        StringBuilder topicBuilder = new StringBuilder(topic);
        if (deployEnv.equals(DEV_SUFFIX)) {
            topicBuilder.append(".").append(DEV_SUFFIX);
        }

        return topicBuilder;
    }
}
