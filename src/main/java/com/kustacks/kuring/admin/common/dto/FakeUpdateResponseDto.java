package com.kustacks.kuring.admin.common.dto;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.Getter;

@Getter
public class FakeUpdateResponseDto extends ResponseDto {

    public FakeUpdateResponseDto() {
        super(true, "성공", 200);
    }
}
