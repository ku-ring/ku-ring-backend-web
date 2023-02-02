package com.kustacks.kuring.common.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.common.dto.AdminMessageDto;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

    public void verifyToken(String token) throws FirebaseMessagingException {
        Message message = Message.builder().setToken(token).build();
        firebaseMessaging.send(message);
    }

    public void subscribe(String token, String topic) throws FirebaseMessagingException, InternalLogicException {

        ArrayList<String> tokens = new ArrayList<>(1);
        tokens.add(token);

        if (deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);

        if (response.getFailureCount() > 0) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_SUBSCRIBE);
        }
    }

    public void unsubscribe(String token, String topic) throws FirebaseMessagingException, InternalLogicException {

        ArrayList<String> tokens = new ArrayList<>(1);
        tokens.add(token);

        if (deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(tokens, topic);

        if (response.getFailureCount() > 0) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_UNSUBSCRIBE);
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
     * @param messageDTO
     * @throws FirebaseMessagingException
     */
    public void sendMessage(NoticeMessageDto messageDTO) throws FirebaseMessagingException {

        Map<String, String> noticeMap = objectMapper.convertValue(messageDTO, Map.class);

        StringBuilder topic = new StringBuilder(messageDTO.getCategory());
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

    public void validationToken(String token) {
        if(!StringUtils.hasText(token)) {
            throw new APIException(ErrorCode.API_INVALID_PARAM);
        }

        try {
            this.verifyToken(token);
        } catch (FirebaseMessagingException exception) {
            if(exception instanceof FirebaseMessagingException) {
                throw new APIException(ErrorCode.API_FB_INVALID_TOKEN);
            } else {
                throw new APIException(ErrorCode.UNKNOWN_ERROR);
            }
        }
    }
}
