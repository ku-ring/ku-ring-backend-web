package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.feedback.Feedback;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.user.User;

import java.util.HashMap;
import java.util.List;

public interface AdminService {

    List<Category> getCategories();
    List<Notice> getNotices();
    List<Feedback> getFeedbacks();
    List<User> getUsers();
    HashMap<String, Category> getCategoryMap();

    void checkToken(String token);
}
