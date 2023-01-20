package com.kustacks.kuring.common.dto;

import lombok.Getter;

@Getter
public class FakeUpdateResponseDto extends ResponseDto {

    public FakeUpdateResponseDto() {
        super(true, "성공", 200);
    }
}
