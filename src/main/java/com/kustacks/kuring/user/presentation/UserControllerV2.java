package com.kustacks.kuring.user.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.common.SubscribeDepartmentsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SUBSCRIBE_SUCCESS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserControllerV2 {

    private final UserService userService;
    private final FirebaseService firebaseService;

    @PostMapping(value = "/departments/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<Void> editUserSubscribeDepartments(@Valid @RequestBody SubscribeDepartmentsRequest request) {
        firebaseService.validationToken(request.getId());
        userService.editSubscribeDepartmentList(request.getId(), request.getDepartments());
        return new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null);
    }
}
