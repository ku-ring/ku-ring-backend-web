package com.kustacks.kuring.user.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.common.dto.SubscribeCategoriesRequest;
import com.kustacks.kuring.user.common.dto.SubscribeDepartmentsRequest;
import com.kustacks.kuring.user.common.dto.SaveFeedbackRequest;
import com.kustacks.kuring.user.facade.UserCommandFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserCommandApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserCommandFacade userCommandFacade;

    @PostMapping(value = "/subscriptions/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeCategories(
            @Valid @RequestBody SubscribeCategoriesRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandFacade.editSubscribeCategories(id, request.getCategories());
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody SubscribeDepartmentsRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandFacade.editSubscribeDepartments(id, request.getDepartments());
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<BaseResponse<Void>> saveFeedback(
            @Valid @RequestBody SaveFeedbackRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandFacade.saveFeedback(id, request.getContent());
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SAVE_SUCCESS, null));
    }
}
