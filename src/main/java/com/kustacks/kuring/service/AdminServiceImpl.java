package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.admin.Admin;
import com.kustacks.kuring.domain.admin.AdminRepository;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.feedback.Feedback;
import com.kustacks.kuring.domain.feedback.FeedbackRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.kuapi.CategoryName;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final FeedbackRepository feedbackRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AdminServiceImpl(AdminRepository adminRepository,
                            FeedbackRepository feedbackRepository,
                            NoticeRepository noticeRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository) {

        this.adminRepository = adminRepository;
        this.feedbackRepository = feedbackRepository;
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
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
    public HashMap<String, Category> getCategoryMap() {
        return (HashMap<String, Category>) categoryRepository.findAllMap();
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
}
