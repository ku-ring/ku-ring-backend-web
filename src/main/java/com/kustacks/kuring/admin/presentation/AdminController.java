package com.kustacks.kuring.admin.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.admin.business.AdminService;
import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.common.annotation.CheckSession;
import com.kustacks.kuring.common.dto.AdminMessageDTO;
import com.kustacks.kuring.common.dto.CategoryDTO;
import com.kustacks.kuring.common.dto.FakeUpdateResponseDTO;
import com.kustacks.kuring.common.dto.LoginResponseDTO;
import com.kustacks.kuring.common.dto.NoticeMessageDTO;
import com.kustacks.kuring.common.dto.ResponseDTO;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Value("${server.deploy.environment}")
    private String deployEnv;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public AdminController(
            CategoryService categoryService,
            FirebaseService firebaseService,
            AdminService adminService,
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

        model.addAttribute("subUnsub", true);
        model.addAttribute("fakeUpdate", "dev".equals(deployEnv));

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

        if (!"dev".equals(deployEnv)) {
            return "thymeleaf/404";
        }

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
        if (fakeNoticeCategory == null || fakeNoticeSubject == null) {
            throw new APIException(ErrorCode.API_ADMIN_MISSING_PARAM);
        }

        fakeNoticeSubject = URLDecoder.decode(fakeNoticeSubject, StandardCharsets.UTF_8);

        Category dbCategory = categoryMap.get(fakeNoticeCategory);
        if (dbCategory == null || fakeNoticeSubject.equals("") || fakeNoticeSubject.length() > 128) {
            throw new APIException(ErrorCode.API_ADMIN_INVALID_SUBJECT);
        }

        LocalDateTime now = LocalDateTime.now();
        String fakeNoticePostedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        log.info("fake articleId = {}", fakeNoticeArticleId);
        log.info("fake postedDate = {}", fakeNoticePostedDate);
        log.info("fake subject = {}", fakeNoticeSubject);
        log.info("fake category = {}", fakeNoticeCategory);

        try {
            firebaseService.sendMessage(NoticeMessageDTO.builder()
                    .articleId(fakeNoticeArticleId)
                    .postedDate(fakeNoticePostedDate)
                    .category(fakeNoticeCategory)
                    .subject(fakeNoticeSubject)
                    .baseUrl(CategoryName.LIBRARY.getName().equals(fakeNoticeCategory) ? libraryBaseUrl : normalBaseUrl)
                    .build());
        } catch (FirebaseMessagingException e) {
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
        if (token == null || token.equals("")) {
            throw new APIException(ErrorCode.API_MISSING_PARAM);
        }

        try {
            adminService.checkToken(token);
        } catch (InternalLogicException e) {
            throw new APIException(ErrorCode.API_ADMIN_UNAUTHENTICATED, e);
        }

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(60 * 120); // 브라우저에서 2시간동안 요청이 없으면 세션 파기

        return LoginResponseDTO.builder()
                .isSuccess(true)
                .resultMsg("성공")
                .resultCode(200).build();
    }

    @ResponseBody
    @PostMapping("/api/fake-update/fcm")
    public FakeUpdateResponseDTO enrollFakeUpdate(@RequestBody Map<String, String> reqBody) {

        String token = reqBody.get("token");
        String auth = reqBody.get("auth");
        String type = reqBody.get("type");

        log.info("fcm 토큰 = {}", token);
        log.info("인증 토큰 = {}\n", auth);

        boolean isAuthenticated = adminService.checkToken(auth);
        if (!isAuthenticated) {
            throw new APIException(ErrorCode.API_ADMIN_UNAUTHENTICATED);
        }

        try {
            firebaseService.verifyToken(token);
        } catch (FirebaseMessagingException e) {
            throw new APIException(ErrorCode.API_ADMIN_INVALID_FCM, e);
        }

        if ("notice".equals(type)) {
            String articleId = reqBody.get("articleId");
            String postedDate = reqBody.get("postedDate");
            String subject = reqBody.get("subject");
            String category = reqBody.get("category");

            log.info("제목 = {}", subject);
            log.info("아이디 = {}", articleId);
            log.info("게시일 = {}", postedDate);
            log.info("카테고리 = {}", category);

            boolean isCategorySupported = adminService.checkCategory(category);
            if (!isCategorySupported) {
                throw new APIException(ErrorCode.API_ADMIN_INVALID_CATEGORY);
            }

            boolean isSubjectValid = adminService.checkSubject(subject);
            if (!isSubjectValid) {
                throw new APIException(ErrorCode.API_ADMIN_INVALID_SUBJECT);
            }

            boolean isPostedDateValid = adminService.checkPostedDate(postedDate);
            if (!isPostedDateValid) {
                throw new APIException(ErrorCode.API_ADMIN_INVALID_POSTED_DATE);
            }

            try {
                firebaseService.sendMessage(token, NoticeMessageDTO.builder()
                        .articleId(articleId)
                        .postedDate(postedDate)
                        .subject(subject)
                        .category(category)
                        .baseUrl(CategoryName.LIBRARY.getName().equals(category) ? libraryBaseUrl : normalBaseUrl)
                        .build());
            } catch (FirebaseMessagingException e) {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        } else if ("admin".equals(type)) {
            String title = reqBody.get("title");
            String body = reqBody.get("body");

            log.info("제목 = {}", title);
            log.info("내용 = {}", body);

            boolean isTitleValid = adminService.checkTitle(title);
            if (!isTitleValid) {
                throw new APIException(ErrorCode.API_ADMIN_INVALID_TITLE);
            }

            boolean isBodyValid = adminService.checkBody(body);
            if (!isBodyValid) {
                throw new APIException(ErrorCode.API_ADMIN_INVALID_BODY);
            }

            try {
                firebaseService.sendMessage(token, AdminMessageDTO.builder()
                        .title(title)
                        .body(body)
                        .build());
            } catch (FirebaseMessagingException e) {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        } else {
            throw new APIException(ErrorCode.API_ADMIN_INVALID_TYPE);
        }

        return new FakeUpdateResponseDTO();
    }
}
