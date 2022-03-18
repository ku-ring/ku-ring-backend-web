package com.kustacks.kuring.service;

import com.kustacks.kuring.mq.dto.AdminMQMessageDTO;
import com.kustacks.kuring.mq.dto.NewNoticeMQMessageDTO;
import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.feedback.Feedback;
import com.kustacks.kuring.persistence.notice.Notice;
import com.kustacks.kuring.persistence.user.User;

import java.util.HashMap;
import java.util.List;

public interface AdminService {

    List<Category> getCategories();
    List<Notice> getNotices();
    List<Feedback> getFeedbacks();
    List<User> getUsers();

    boolean checkToken(String token);
    boolean checkSubject(String subject);
    boolean checkCategory(String category);
    boolean checkPostedDate(String postedDate);
    boolean checkTitle(String title);
    boolean checkBody(String body);

    void sendFBMessage(NewNoticeMQMessageDTO messageDTO);
    void sendFBMessage(String token, NewNoticeMQMessageDTO messageDTO);
    void sendFBMessage(AdminMQMessageDTO messageDTO);
    void sendFBMessage(String token, AdminMQMessageDTO messageDTO);
}
