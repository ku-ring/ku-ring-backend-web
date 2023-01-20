package com.kustacks.kuring.common.dto;

import lombok.Getter;

@Getter
public class InsertFeedbackResponseDto extends ResponseDto {

    public InsertFeedbackResponseDto() {
        super(true, "성공", 201);
    }
}
