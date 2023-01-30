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


    public void saveFeedback(String token, String content) {
        User user = userRepository.findByToken(token);
        if(user == null) {
            user = userRepository.save(new User(token));
        }

        feedbackRepository.save(new Feedback(content, user));
    }
}
