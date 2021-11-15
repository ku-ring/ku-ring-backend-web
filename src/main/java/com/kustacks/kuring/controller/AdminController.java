package com.kustacks.kuring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.annotation.CheckSession;
import com.kustacks.kuring.controller.dto.CategoryDTO;
import com.kustacks.kuring.controller.dto.LoginResponseDTO;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.controller.dto.ResponseDTO;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.feedback.Feedback;
import com.kustacks.kuring.domain.feedback.FeedbackRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final FirebaseService firebaseService;
    private final AdminService adminService;

    private final ObjectMapper objectMapper;
    private final Map<String, Category> categoryMap;

    public AdminController(
            CategoryServiceImpl categoryService,
            FirebaseService firebaseService,
            AdminServiceImpl adminService,
            ObjectMapper objectMapper) {

        this.categoryService = categoryService;
        this.firebaseService = firebaseService;
        this.adminService = adminService;

        this.objectMapper = objectMapper;
        this.categoryMap = adminService.getCategoryMap();
    }


    @CheckSession
    @GetMapping("/{type}")
    public String dashboardPage(Model model, @PathVariable String type) throws JsonProcessingException {

        List<Feedback> feedbacks = null;
        List<Notice> notices = null;
        List<User> users = null;
        List<CategoryDTO> categoryDTOList = null;

        model.addAttribute("title", "KU Ring");

        switch (type) {
            case "dashboard":
                feedbacks = adminService.getFeedbacks();
                notices = adminService.getNotices();
                categoryDTOList = categoryService.getCategoryDTOList();
                users = adminService.getUsers();
                break;
            case "user":
                users = adminService.getUsers();
                break;
            case "feedback":
                feedbacks = adminService.getFeedbacks();
                break;
            case "notice":
                notices = adminService.getNotices();
                categoryDTOList = categoryService.getCategoryDTOList();
                break;
            default:
                break;
        }

        model.addAttribute("feedbacks", feedbacks);

        model.addAttribute("notices", notices);
        String jsonCategories = objectMapper.writeValueAsString(categoryDTOList);
        model.addAttribute("categories", jsonCategories);

        model.addAttribute("users", users);

        model.addAttribute("subUnsub", type.equals("dashboard"));
        model.addAttribute("fakeUpdate", type.equals("dashboard"));

        return "thymeleaf/main";
    }

    @CheckSession
    @GetMapping("/service/sub-unsub")
    public String subUnsubPage(Model model) throws JsonProcessingException {

        List<CategoryDTO> categoryDTOList = categoryService.getCategoryDTOList();

        model.addAttribute("subUnsub", true);
        model.addAttribute("fakeUpdate", false);

        String jsonCategories = objectMapper.writeValueAsString(categoryDTOList);
        model.addAttribute("categories", jsonCategories);

        return "thymeleaf/main";
    }

    @CheckSession
    @GetMapping("/service/fake-update")
    public String fakeUpdatePage(Model model) throws JsonProcessingException {

        List<CategoryDTO> categoryDTOList = categoryService.getCategoryDTOList();

        model.addAttribute("subUnsub", false);
        model.addAttribute("fakeUpdate", true);

        String jsonCategories = objectMapper.writeValueAsString(categoryDTOList);
        model.addAttribute("categories", jsonCategories);

        return "thymeleaf/main";
    }

    @CheckSession
    @ResponseBody
    @PostMapping(value = "/service/fake-update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO enrollFakeNotice(@RequestBody HashMap<String, String> requestBody) {

        String fakeNoticeCategory = requestBody.get("category");
        String fakeNoticeSubject = requestBody.get("subject");
        String fakeNoticeArticleId = requestBody.get("articleId");
        if(fakeNoticeCategory == null || fakeNoticeSubject == null) {
            throw new APIException(ErrorCode.API_MISSING_PARAM);
        }

        fakeNoticeSubject = URLDecoder.decode(fakeNoticeSubject, StandardCharsets.UTF_8);

        Category dbCategory = categoryMap.get(fakeNoticeCategory);
        if(dbCategory == null || fakeNoticeSubject.equals("") || fakeNoticeSubject.length() > 128) {
            throw new APIException(ErrorCode.API_INVALID_PARAM);
        }

        LocalDateTime now = LocalDateTime.now();
        String fakeNoticePostedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("fake articleId = {}", fakeNoticeArticleId);
        log.info("fake postedDate = {}", fakeNoticePostedDate);
        log.info("fake subject = {}", fakeNoticeSubject);
        log.info("fake category = {}", fakeNoticeCategory);

        try {
            firebaseService.sendMessage(NoticeDTO.builder()
                    .articleId(fakeNoticeArticleId)
                    .postedDate(fakeNoticePostedDate)
                    .categoryName(fakeNoticeCategory)
                    .subject(fakeNoticeSubject)
                    .build());
        } catch(FirebaseMessagingException e) {
            throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
        }

        return new ResponseDTO(true, "성공", 200);
    }

    @CheckSession(isSessionRequired = false)
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        return "thymeleaf/login";
    }

    @CheckSession(isSessionRequired = false)
    @ResponseBody
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody HashMap<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {

        String token = requestBody.get("token");
        if(token == null || token.equals("")) {
            throw new APIException(ErrorCode.API_MISSING_PARAM);
        }

        try {
            adminService.checkToken(token);
        } catch(InternalLogicException e) {
            throw new APIException(ErrorCode.API_AD_UNAUTHENTICATED, e);
        }

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(60 * 120); // 브라우저에서 2시간동안 요청이 없으면 세션 파기

        return LoginResponseDTO.builder()
                .isSuccess(true)
                .resultMsg("성공")
                .resultCode(200).build();
    }
}
