package com.kustacks.kuring.feedback.presentation;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.InsertFeedbackResponseDTO;
import com.kustacks.kuring.controller.dto.SaveFeedbackRequestDTO;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.service.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedbackController {

    private final FirebaseService firebaseService;
    private final FeedbackService feedbackService;

    public FeedbackController(
            FirebaseService firebaseService,
            FeedbackServiceImpl feedbackService) {

        this.firebaseService = firebaseService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback")
    public InsertFeedbackResponseDTO insertFeedback(@RequestBody SaveFeedbackRequestDTO requestDTO) throws APIException {
        String token = requestDTO.getToken();
        String content = requestDTO.getContent();

        if(token == null || content == null) {
            throw new APIException(ErrorCode.API_MISSING_PARAM);
        }

        if(content.length() < 5 || content.length() > 256) {
            throw new APIException(ErrorCode.API_FD_INVALID_CONTENT);
        }

        try {
            firebaseService.verifyToken(token);
        } catch(Exception e) {
            if(e instanceof FirebaseMessagingException) {
                throw new APIException(ErrorCode.API_FB_INVALID_TOKEN);
            } else {
                throw new APIException(ErrorCode.UNKNOWN_ERROR);
            }
        }

        feedbackService.insertFeedback(token, content);

        return InsertFeedbackResponseDTO.builder().build();
    }
}
