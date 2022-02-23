package com.kustacks.kuring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseService {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    @Value("${server.deploy.environment}")
    private String deployEnv;

    private final String DEV_SUFFIX = ".dev";

    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;

    FirebaseService(ObjectMapper objectMapper,  @Value("${firebase.file-path}") String filePath) throws IOException {

        this.objectMapper = objectMapper;

        ClassPathResource resource = new ClassPathResource(filePath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
        firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
    }

    public void verifyToken(String token) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(token)
                .build();

        firebaseMessaging.send(message);
    }

    public void subscribe(String token, String topic) throws FirebaseMessagingException, InternalLogicException {

        ArrayList<String> tokens = new ArrayList<>(1);
        tokens.add(token);

        if(deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);

        if(response.getFailureCount() > 0) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_SUBSCRIBE);
        }
    }

    public void unsubscribe(String token, String topic) throws FirebaseMessagingException, InternalLogicException {

        ArrayList<String> tokens = new ArrayList<>(1);
        tokens.add(token);

        if(deployEnv.equals("dev")) {
            topic = topic + DEV_SUFFIX;
        }

        TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(tokens, topic);

        if(response.getFailureCount() > 0) {
            throw new InternalLogicException(ErrorCode.FB_FAIL_UNSUBSCRIBE);
        }
    }


    /**
     * Firebase message에는 두 가지 paylaad가 존재한다.
     * 1. notification
     * 2. data
     *
     * notification을 Message로 만들어 보내면 여기서 설정한 title, body가 직접 앱 noti로 뜬다.
     * data로 Message를 만들어 보내면 이것을 앱 클라이언트(Andriod)가 받아서, 가공한 뒤 푸쉬 알람으로 만들 수 있다.
     *
     * 따라서 여기선 putData를 사용하여 보내고, 클라이언트가 푸쉬 알람을 만들어 띄운다.
     *
     * @param newNotice
     * @throws FirebaseMessagingException
     */

    public void sendMessage(NoticeDTO newNotice) throws FirebaseMessagingException {

        Map<String, String> noticeMap = noticeDtoToMap(newNotice);
        noticeMap.put(
                "baseUrl",
                newNotice.getCategoryName().equals(CategoryName.LIBRARY.getName()) ? libraryBaseUrl : normalBaseUrl);

        StringBuilder topic = new StringBuilder(newNotice.getCategoryName());
        if(deployEnv.equals("dev")) {
            topic.append(DEV_SUFFIX);
        }

        Message newMessage = Message.builder()
                .putAllData(noticeMap)
                .setTopic(topic.toString())
                .build();

        firebaseMessaging.send(newMessage);
    }

    public void sendMessage(List<NoticeDTO> newNoticeDTOList) throws FirebaseMessagingException {
        for (NoticeDTO noticeDTO : newNoticeDTOList) {
            sendMessage(noticeDTO);
        }
    }

    public void sendMessage(String token, NoticeDTO newNotice) throws FirebaseMessagingException {

        Map<String, String> noticeMap = noticeDtoToMap(newNotice);
        noticeMap.put(
                "baseUrl",
                newNotice.getCategoryName().equals(CategoryName.LIBRARY.getName()) ? libraryBaseUrl : normalBaseUrl);

        Message newMessage = Message.builder()
                .putAllData(noticeMap)
                .setToken(token)
                .build();

        firebaseMessaging.send(newMessage);
    }

    private Map<String, String> noticeDtoToMap(NoticeDTO newNotice) {
        return objectMapper.convertValue(newNotice, Map.class);
    }
}
