package com.kustacks.kuring.admin.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.admin.business.AdminService;
import com.kustacks.kuring.admin.common.dto.CategoryNameAdminDto;
import com.kustacks.kuring.admin.common.dto.FakeUpdateResponseDto;
import com.kustacks.kuring.admin.common.dto.LoginResponseDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.common.annotation.CheckSession;
import com.kustacks.kuring.common.dto.AdminMessageDto;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.common.dto.ResponseDto;
import com.kustacks.kuring.common.exception.APIException;
import com.kustacks.kuring.common.exception.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.common.firebase.exception.FirebaseMessageSendException;
import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminControllerV1 {

    private final FirebaseService firebaseService;
    private final AdminService adminService;

    private final ObjectMapper objectMapper;

    @Value("${server.deploy.environment}")
    private String deployEnv;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public AdminControllerV1(FirebaseService firebaseService, AdminService adminService, ObjectMapper objectMapper) {
        this.firebaseService = firebaseService;
        this.adminService = adminService;
        this.objectMapper = objectMapper;
    }

    @CheckSession
    @GetMapping("/{type}")
    public String dashboardPage(Model model, @PathVariable String type) throws JsonProcessingException {

        List<Feedback> feedbacks = null;
        List<Notice> notices = null;
        List<User> users = null;
        List<CategoryNameAdminDto> categoryNameAdminDtoList = null;

        model.addAttribute("title", "KU Ring");

        switch (type) {
            case "dashboard":
                feedbacks = adminService.getFeedbacks();
                notices = adminService.getNotices();
                categoryNameAdminDtoList = this.getCategoryDtoList();
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
                categoryNameAdminDtoList = this.getCategoryDtoList();
                break;
            default:
                break;
        }

        model.addAttribute("feedbacks", feedbacks);

        model.addAttribute("notices", notices);
        String jsonCategories = objectMapper.writeValueAsString(categoryNameAdminDtoList);
        model.addAttribute("categories", jsonCategories);

        model.addAttribute("users", users);

        model.addAttribute("subUnsub", true);
        model.addAttribute("fakeUpdate", "dev".equals(deployEnv));

        return "thymeleaf/main";
    }

    @CheckSession
    @GetMapping("/service/sub-unsub")
    public String subUnsubPage(Model model) throws JsonProcessingException {

        List<CategoryNameAdminDto> categoryNameAdminDtoList = this.getCategoryDtoList();

        model.addAttribute("subUnsub", true);
        model.addAttribute("fakeUpdate", false);

        String jsonCategories = objectMapper.writeValueAsString(categoryNameAdminDtoList);
        model.addAttribute("categories", jsonCategories);

        return "thymeleaf/main";
    }

    @CheckSession
    @GetMapping("/service/fake-update")
    public String fakeUpdatePage(Model model) throws JsonProcessingException {

        if (!"dev".equals(deployEnv)) {
            return "thymeleaf/404";
        }

        List<CategoryNameAdminDto> categoryNameAdminDtoList = this.getCategoryDtoList();

        model.addAttribute("subUnsub", false);
        model.addAttribute("fakeUpdate", true);

        String jsonCategories = objectMapper.writeValueAsString(categoryNameAdminDtoList);
        model.addAttribute("categories", jsonCategories);

        return "thymeleaf/main";
    }

    @CheckSession
    @ResponseBody
    @PostMapping(value = "/service/fake-update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto enrollFakeNotice(@RequestBody HashMap<String, String> requestBody) {

        String fakeNoticeCategory = requestBody.get("category");
        String fakeNoticeSubject = requestBody.get("subject");
        String fakeNoticeArticleId = requestBody.get("articleId");
        if (fakeNoticeCategory == null || fakeNoticeSubject == null) {
            throw new APIException(ErrorCode.API_ADMIN_MISSING_PARAM);
        }

        fakeNoticeSubject = URLDecoder.decode(fakeNoticeSubject, StandardCharsets.UTF_8);

        CategoryName dbCategoryName = CategoryName.fromStringName(fakeNoticeCategory);
        if (dbCategoryName == null || fakeNoticeSubject.equals("") || fakeNoticeSubject.length() > 128) {
            throw new APIException(ErrorCode.API_ADMIN_INVALID_SUBJECT);
        }

        LocalDateTime now = LocalDateTime.now();
        String fakeNoticePostedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        log.info("fake articleId = {}", fakeNoticeArticleId);
        log.info("fake postedDate = {}", fakeNoticePostedDate);
        log.info("fake subject = {}", fakeNoticeSubject);
        log.info("fake category = {}", fakeNoticeCategory);

        try {
            firebaseService.sendMessage(NoticeMessageDto.builder()
                    .articleId(fakeNoticeArticleId)
                    .postedDate(fakeNoticePostedDate)
                    .category(fakeNoticeCategory)
                    .subject(fakeNoticeSubject)
                    .categoryKorName("fakeCategoryKorName")
                    .baseUrl(CategoryName.LIBRARY.getName().equals(fakeNoticeCategory) ? libraryBaseUrl : normalBaseUrl)
                    .build());
        } catch (FirebaseMessageSendException e) {
            throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
        }

        return new ResponseDto(true, "성공", 200);
    }

    @CheckSession(isSessionRequired = false)
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        return "thymeleaf/login";
    }

    @CheckSession(isSessionRequired = false)
    @ResponseBody
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody HashMap<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {

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

        return new LoginResponseDto(true, "성공", 200);
    }

    @ResponseBody
    @PostMapping("/api/fake-update/fcm")
    public FakeUpdateResponseDto enrollFakeUpdate(@RequestBody Map<String, String> reqBody) {

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
            firebaseService.validationToken(token);
        } catch (FirebaseInvalidTokenException e) {
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
                firebaseService.sendNoticeMessageForAdmin(token, NoticeMessageDto.builder()
                        .articleId(articleId)
                        .postedDate(postedDate)
                        .subject(subject)
                        .category(category)
                        .categoryKorName("fakeCategoryKorName")
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
                firebaseService.sendNoticeMessageForAdmin(token, new AdminMessageDto(title, body));
            } catch (FirebaseMessagingException e) {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        } else {
            throw new APIException(ErrorCode.API_ADMIN_INVALID_TYPE);
        }

        return new FakeUpdateResponseDto();
    }

    public List<CategoryNameAdminDto> getCategoryDtoList() {
        return Stream.of(CategoryName.values())
                .map(categoryName -> new CategoryNameAdminDto(categoryName.getName()))
                .collect(Collectors.toList());
    }
}
