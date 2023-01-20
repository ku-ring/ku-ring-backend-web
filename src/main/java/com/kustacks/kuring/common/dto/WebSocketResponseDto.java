package com.kustacks.kuring.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebSocketResponseDto extends ResponseDto {

    private String type;

    public WebSocketResponseDto(boolean isSuccess, String resultMsg, int resultCode, String type) {
        super(isSuccess, resultMsg, resultCode);
        this.type = type;
    }
}
