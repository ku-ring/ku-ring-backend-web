package com.kustacks.kuring.feedback.business;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.feedback.domain.FeedbackRepository;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.message.firebase.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final FirebaseService firebaseService;

    public void saveFeedback(String token, String content) {
        Optional<User> optionalUser = userRepository.findByToken(token);
        if(optionalUser.isEmpty()) {
            optionalUser = Optional.of(userRepository.save(new User(token)));
            firebaseService.subscribe(token, ALL_DEVICE_SUBSCRIBED_TOPIC);
        }

        User findUser = optionalUser.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        findUser.addFeedback(content);
    }

    public List<FeedbackDto> lookupFeedbacks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        return feedbackRepository.findAll(pageRequest)
                .stream()
                .map(FeedbackDto::from)
                .collect(Collectors.toList());
    }
}
