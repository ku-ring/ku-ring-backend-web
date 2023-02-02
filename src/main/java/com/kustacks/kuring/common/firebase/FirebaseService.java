package com.kustacks.kuring.common.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.common.dto.AdminMessageDto;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.common.firebase.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.common.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.common.firebase.exception.FirebaseUnSubscribeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final String DEV_SUFFIX = ".dev";
    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;

    @Value("${server.deploy.environment}")
    private String deployEnv;

    public void validationToken(String token) {
        try {
            Message message = Message.builder().setToken(token).build();
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseInvalidTokenException();
        }
    }

    public void subscribe(String token, String topic) throws FirebaseSubscribeException {
        if (deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response;
        try{
            response = firebaseMessaging.subscribeToTopic(List.of(token), topic);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseSubscribeException();
        }

        if (response.getFailureCount() > 0) {
            throw new FirebaseSubscribeException();
        }
    }

    public void unsubscribe(String token, String topic) throws FirebaseUnSubscribeException {
        if (deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response;
        try{
            response = firebaseMessaging.unsubscribeFromTopic(List.of(token), topic);
        } catch (FirebaseMessagingException exception) {
            throw new FirebaseUnSubscribeException();
        }

        if (response.getFailureCount() > 0) {
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
     * @throws FirebaseMessagingException
     */
    public void sendMessage(NoticeMessageDto messageDto) throws FirebaseMessagingException {

        Map<String, String> noticeMap = objectMapper.convertValue(messageDto, Map.class);

        StringBuilder topic = new StringBuilder(messageDto.getCategory());
        if (deployEnv.equals("dev")) {
            topic.append(DEV_SUFFIX);
        }

        Message newMessage = Message.builder()
                .putAllData(noticeMap)
                .setTopic(topic.toString())
                .build();

        firebaseMessaging.send(newMessage);
    }

    public void sendMessage(List<NoticeMessageDto> messageDtoList) throws FirebaseMessagingException {
        for (NoticeMessageDto messageDTO : messageDtoList) {
            sendMessage(messageDTO);
        }
    }

    public void sendMessage(String token, NoticeMessageDto messageDto) throws FirebaseMessagingException {
        Message newMessage = Message.builder()
                .putAllData(objectMapper.convertValue(messageDto, Map.class))
                .setToken(token)
                .build();

        firebaseMessaging.send(newMessage);
    }

    public void sendMessage(String token, AdminMessageDto messageDto) throws FirebaseMessagingException {
        Message newMessage = Message.builder()
                .putAllData(objectMapper.convertValue(messageDto, Map.class))
                .setToken(token)
                .build();

        firebaseMessaging.send(newMessage);
    }
}
