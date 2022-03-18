package com.kustacks.kuring.service;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.mq.MQNotifierProducer;
import com.kustacks.kuring.mq.dto.AdminMQMessageDTO;
import com.kustacks.kuring.mq.dto.NewNoticeMQMessageDTO;
import com.kustacks.kuring.persistence.admin.Admin;
import com.kustacks.kuring.persistence.admin.AdminRepository;
import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.category.CategoryRepository;
import com.kustacks.kuring.persistence.feedback.Feedback;
import com.kustacks.kuring.persistence.feedback.FeedbackRepository;
import com.kustacks.kuring.persistence.notice.Notice;
import com.kustacks.kuring.persistence.notice.NoticeRepository;
import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.persistence.user.UserRepository;
import com.kustacks.kuring.CategoryName;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final FeedbackRepository feedbackRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MQNotifierProducer<NewNoticeMQMessageDTO> mqNoticeNotifierProducer;
    private final MQNotifierProducer<AdminMQMessageDTO> mqAdminNotifierProducer;

    public AdminServiceImpl(AdminRepository adminRepository,
                            FeedbackRepository feedbackRepository,
                            NoticeRepository noticeRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            MQNotifierProducer<NewNoticeMQMessageDTO> rabbitMQNoticeNotifierProducer,
                            MQNotifierProducer<AdminMQMessageDTO> rabbitMQAdminNotifierProducer
    ) {

        this.adminRepository = adminRepository;
        this.feedbackRepository = feedbackRepository;
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.mqNoticeNotifierProducer = rabbitMQNoticeNotifierProducer;
        this.mqAdminNotifierProducer = rabbitMQAdminNotifierProducer;
    }


    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Notice> getNotices() {
        return noticeRepository.findAll();
    }

    @Override
    public List<Feedback> getFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean checkToken(String token) {
        Admin admin = adminRepository.findByToken(token);
        return admin != null;
    }

    @Override
    public boolean checkSubject(String subject) {
        return subject.length() > 0 && subject.length() <= 128;
    }

    @Override
    public boolean checkCategory(String category) {
        boolean isSupported = false;
        for (CategoryName categoryName : CategoryName.values()) {
            if(categoryName.getName().equals(category) || categoryName.getKorName().equals(category)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    @Override
    public boolean checkPostedDate(String postedDate) {
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(postedDate);
        } catch(ParseException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean checkTitle(String title) {
        return title != null && title.length() > 0;
    }

    @Override
    public boolean checkBody(String body) {
        return body != null && body.length() > 0;
    }

    @Override
    public void sendFBMessage(NewNoticeMQMessageDTO messageDTO) {
        mqNoticeNotifierProducer.publish(messageDTO);
    }

    @Override
    public void sendFBMessage(String token, NewNoticeMQMessageDTO messageDTO) {
        messageDTO.setToken(token);
        mqNoticeNotifierProducer.publish(messageDTO);
    }

    @Override
    public void sendFBMessage(AdminMQMessageDTO messageDTO) {
        mqAdminNotifierProducer.publish(messageDTO);
    }

    @Override
    public void sendFBMessage(String token, AdminMQMessageDTO messageDTO) {
        messageDTO.setToken(token);
        mqAdminNotifierProducer.publish(messageDTO);
    }
}
