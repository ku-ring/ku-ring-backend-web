package com.kustacks.kuring.admin.facade;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kustacks.kuring.admin.business.AdminDetailsService;
import com.kustacks.kuring.admin.common.dto.AdminNotificationDto;
import com.kustacks.kuring.admin.common.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.common.dto.TestNotificationRequest;
import com.kustacks.kuring.admin.presentation.NoticeProperties;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.userdetails.AdminUserDetails;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommandFacade {

    private static final String ALL_DEVICE_SUBSCRIBED_TOPIC = "allDevice";

    private final FirebaseService firebaseService;
    private final NoticeProperties noticeProperties;
    private final AdminDetailsService adminDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void createTestNotice(TestNotificationRequest request) {
        String testNoticePostedDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        CategoryName testCategoryName = CategoryName.fromStringName(request.getCategory());

        firebaseService.sendNotification(NoticeMessageDto.builder()
                .articleId(request.getArticleId())
                .postedDate(testNoticePostedDate)
                .category(testCategoryName.getName())
                .subject(request.getSubject())
                .categoryKorName(testCategoryName.getKorName())
                .baseUrl(CategoryName.LIBRARY.equals(testCategoryName) ? noticeProperties.getLibraryBaseUrl() : noticeProperties.getNormalBaseUrl())
                .build());
    }

    @Transactional(readOnly = true)
    public void createRealNoticeForAllUser(RealNotificationRequest request, Authentication authentication) {
        AdminUserDetails adminUserDetails = (AdminUserDetails) adminDetailsService
                .loadUserByUsername(authentication.getPrincipal().toString());

        if(!passwordEncoder.matches(request.getAdminPassword(), adminUserDetails.getPassword())) {
            throw new IllegalArgumentException("관리자 비밀번호가 일치하지 않습니다.");
        }

        firebaseService.sendNotificationByAdmin(new AdminNotificationDto(request.getTitle(), request.getBody()));
    }

    /**
     * 1회성 API 단 한번 모든 사용자를 공통 topic에 구독시킨 후 제거 예정
     */
    @Transactional(readOnly = true)
    public void subscribeAllUserSameTopic() {
        FirebaseMessaging instance = FirebaseMessaging.getInstance();
        List<String> allToken = userRepository.findAllToken();
        int size = allToken.size();

        for(int i = 0; i < size; i += 500) {
            List<String> subList = allToken.subList(i, Math.min(i + 500, size));
            instance.subscribeToTopicAsync(subList, ALL_DEVICE_SUBSCRIBED_TOPIC);
        }
    }
}
