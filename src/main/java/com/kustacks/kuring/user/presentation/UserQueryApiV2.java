package com.kustacks.kuring.user.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.notice.common.dto.CategoryNameDto;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.user.facade.UserQueryFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserQueryApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserQueryFacade userQueryFacade;

    @GetMapping("/subscriptions/categories")
    public ResponseEntity<BaseResponse<List<CategoryNameDto>>> lookupUserSubscribeCategories(@RequestHeader(USER_TOKEN_HEADER_KEY) String id) {
        List<CategoryNameDto> categoryNameDtos = userQueryFacade.lookupSubscribeCategories(id);
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS, categoryNameDtos));
    }

    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<DepartmentNameDto>>> lookupUserSubscribeDepartments(@RequestHeader(USER_TOKEN_HEADER_KEY) String id) {
        List<DepartmentNameDto> departmentNameDtos = userQueryFacade.lookupSubscribeDepartments(id);
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS, departmentNameDtos));
    }
}
