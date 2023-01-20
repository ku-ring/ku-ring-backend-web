package com.kustacks.kuring.service;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.user.domain.User;

import java.util.HashMap;
import java.util.List;

public interface AdminService {

    List<Category> getCategories();
    List<Notice> getNotices();
    List<Feedback> getFeedbacks();
    List<User> getUsers();
    HashMap<String, Category> getCategoryMap();

    boolean checkToken(String token);
    boolean checkSubject(String subject);
    boolean checkCategory(String category);
    boolean checkPostedDate(String postedDate);
    boolean checkTitle(String title);
    boolean checkBody(String body);
}
