package com.kustacks.kuring.feedback.presentation;

import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.feedback.business.FeedbackService;
import com.kustacks.kuring.feedback.common.dto.request.SaveFeedbackRequest;
import com.kustacks.kuring.feedback.common.dto.response.SaveFeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedbackController {

    private final FirebaseService firebaseService;
    private final FeedbackService feedbackService;

    @PostMapping("/feedback")
    public SaveFeedbackResponse saveFeedback(@Valid @RequestBody SaveFeedbackRequest request) throws APIException {
        String token = request.getId();
        String content = request.getContent();
        if (content.length() < 5 || content.length() > 256) {
            throw new APIException(ErrorCode.API_FD_INVALID_CONTENT);
        }

        firebaseService.validationToken(token);
        feedbackService.saveFeedback(token, content);

        return new SaveFeedbackResponse();
    }
}
