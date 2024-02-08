package com.kustacks.kuring.staff.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.staff.adapter.in.web.dto.StaffSearchListResponse;
import com.kustacks.kuring.staff.adapter.in.web.dto.StaffSearchResponse;
import com.kustacks.kuring.staff.application.port.in.StaffQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.STAFF_SEARCH_SUCCESS;

@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/staffs")
public class StaffQueryApiV2 {

    private final StaffQueryUseCase staffQueryUseCase;

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<StaffSearchListResponse>> searchStaff(@NotBlank @RequestParam String content) {
        List<StaffSearchResponse> staffSearchResults = staffQueryUseCase.findAllStaffByContent(content)
                .stream()
                .map(StaffSearchResponse::from)
                .toList();

        StaffSearchListResponse response = new StaffSearchListResponse(staffSearchResults);
        return ResponseEntity.ok().body(new BaseResponse<>(STAFF_SEARCH_SUCCESS, response));
    }
}
