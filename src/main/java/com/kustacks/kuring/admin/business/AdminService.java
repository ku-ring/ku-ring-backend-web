package com.kustacks.kuring.admin.business;

import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.domain.AdminRepository;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.feedback.domain.FeedbackRepository;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.category.domain.CategoryName;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final FeedbackRepository feedbackRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AdminService(AdminRepository adminRepository,
                        FeedbackRepository feedbackRepository,
                        NoticeRepository noticeRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository
    ) {

        this.adminRepository = adminRepository;
        this.feedbackRepository = feedbackRepository;
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Notice> getNotices() {
        return noticeRepository.findAll();
    }

    public List<Feedback> getFeedbacks() {
        return feedbackRepository.findAll();
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }


    public HashMap<String, Category> getCategoryMap() {
        return (HashMap<String, Category>) categoryRepository.findAllMap();
    }


    public boolean checkToken(String token) {
        Admin admin = adminRepository.findByToken(token);
        return admin != null;
    }


    public boolean checkSubject(String subject) {
        return subject.length() > 0 && subject.length() <= 128;
    }


    public boolean checkCategory(String category) {
        boolean isSupported = false;
        for (CategoryName categoryName : CategoryName.values()) {
            if (categoryName.getName().equals(category) || categoryName.getKorName().equals(category)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }


    public boolean checkPostedDate(String postedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(postedDate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }


    public boolean checkTitle(String title) {
        return title != null && title.length() > 0;
    }


    public boolean checkBody(String body) {
        return body != null && body.length() > 0;
    }
}
