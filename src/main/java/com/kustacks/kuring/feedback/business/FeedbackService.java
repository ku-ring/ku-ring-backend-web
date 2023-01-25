package com.kustacks.kuring.feedback.business;

import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.feedback.domain.FeedbackRepository;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackService(
            FeedbackRepository feedbackRepository,
            UserRepository userRepository) {

        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }


    public void insertFeedback(String token, String content) {

        User user = userRepository.findByToken(token);
        if(user == null) {
            user = userRepository.save(User.builder().token(token).build());
        }

        feedbackRepository.save(Feedback.builder()
                .user(user)
                .content(content)
                .build());
    }
}
