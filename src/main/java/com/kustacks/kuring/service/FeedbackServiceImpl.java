package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.feedback.Feedback;
import com.kustacks.kuring.domain.feedback.FeedbackRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
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
