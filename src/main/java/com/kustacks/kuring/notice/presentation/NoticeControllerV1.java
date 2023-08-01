package com.kustacks.kuring.notice.presentation;

import com.kustacks.kuring.notice.presentation.dto.CategoryListResponse;
import com.kustacks.kuring.notice.presentation.dto.SubscribeCategoriesV1Request;
import com.kustacks.kuring.notice.presentation.dto.SubscribeCategoriesResponse;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.NoticeListResponse;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.facade.UserCommandFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/notice", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeControllerV1 {

    private final NoticeService noticeService;
    private final FirebaseService firebaseService;
    private final UserService userService;
    private final UserCommandFacade userCommandFacade;

    @GetMapping
    public NoticeListResponse getNotices(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "offset") @Min(0) int offset,
            @RequestParam(name = "max") @Min(1) @Max(30) int max) {
        return noticeService.getNotices(type, offset, max);
    }

    @GetMapping("/categories")
    public CategoryListResponse getSupportedCategories() {
        List<String> categoryNames = Stream.of(CategoryName.values())
                .map(CategoryName::getName)
                .collect(Collectors.toList());

        return new CategoryListResponse(categoryNames);
    }

    @GetMapping("/subscribe")
    public CategoryListResponse getUserCategories(@RequestParam("id") @NotBlank String token) {
        firebaseService.validationToken(token);
        List<String> categoryNames = userService.lookUpUserCategories(token)
                .stream()
                .map(CategoryName::getName)
                .collect(Collectors.toList());

        return new CategoryListResponse(categoryNames);
    }

    @PostMapping(value = "/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubscribeCategoriesResponse editUserSubscribeCategories(@Valid @RequestBody SubscribeCategoriesV1Request request) {
        userCommandFacade.editSubscribeCategories(request.getId(), request.getCategories());
        return new SubscribeCategoriesResponse();
    }
}
