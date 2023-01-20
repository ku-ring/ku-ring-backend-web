package com.kustacks.kuring.common.dto;

import lombok.Getter;

@Getter
public class FakeUpdateResponseDTO extends ResponseDTO {

    public FakeUpdateResponseDTO() {
        super(true, "성공", 200);
    }
}
