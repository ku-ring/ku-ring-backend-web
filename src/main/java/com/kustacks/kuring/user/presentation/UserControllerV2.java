package com.kustacks.kuring.user.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.common.SubscribeDepartmentsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SUBSCRIBE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_USER_LOOKUP_SUCCESS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserControllerV2 {

    private final UserService userService;
    private final FirebaseService firebaseService;

    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<DepartmentNameDto>>> lookupUserSubscribeDepartments(@RequestHeader("User-Token") String id) {
        firebaseService.validationToken(id);
        List<DepartmentNameDto> departmentDtos = userService.lookupSubscribeDepartmentList(id);
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_LOOKUP_SUCCESS, departmentDtos));
    }

    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody SubscribeDepartmentsRequest request,
            @RequestHeader("User-Token") String id
    ) {
        firebaseService.validationToken(id);
        userService.editSubscribeDepartmentList(id, request.getDepartments());
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }
}
