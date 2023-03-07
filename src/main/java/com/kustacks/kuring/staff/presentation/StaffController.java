package com.kustacks.kuring.staff.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.staff.business.StaffService;
import com.kustacks.kuring.staff.common.dto.response.StaffLookupResponse;
import com.kustacks.kuring.staff.common.dto.response.StaffSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.STAFF_SEARCH_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/api/v2/staffs/search")
    public ResponseEntity<BaseResponse<StaffLookupResponse>> searchStaff(@NotBlank @RequestParam String content) {
        List<StaffSearchDto> staffDtoList = staffService.findAllStaffByContent(content);
        StaffLookupResponse response = new StaffLookupResponse(staffDtoList);
        return ResponseEntity.ok().body(new BaseResponse(STAFF_SEARCH_SUCCESS, response));
    }
}
