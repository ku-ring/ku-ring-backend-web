package com.kustacks.kuring.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.kuapi.FirebaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private FirebaseService firebaseService;

    public TestController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @GetMapping("/test/noti")
    public String notiTest() {

        NoticeDTO notice = NoticeDTO.builder()
                .articleId("5b48c78")
                .categoryName("bachelor")
                .postedDate("20211015")
                .subject("2021학년도 동계 계절학기 개설 계획")
                .build();

        try {
            firebaseService.sendMessage(notice);
            return "FCM으로 메세지를 전송했습니다..!";
        } catch (FirebaseMessagingException e) {
            return "앗.. 실패했습니다. 관우에게 문의해주세용";
        }
    }
}
