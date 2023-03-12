package com.kustacks.kuring.admin.common.dto;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponseDto extends ResponseDto {

    private final String dashboardUrl = "/admin/dashboard";

    public LoginResponseDto(boolean isSuccess, String resultMsg, int resultCode) {
        super(isSuccess, resultMsg, resultCode);
    }
}
