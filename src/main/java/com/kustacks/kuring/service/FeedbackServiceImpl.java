package com.kustacks.kuring.service;

import com.kustacks.kuring.persistence.feedback.Feedback;
import com.kustacks.kuring.persistence.feedback.FeedbackRepository;
import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.persistence.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            UserRepository userRepository) {

        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @Override
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
