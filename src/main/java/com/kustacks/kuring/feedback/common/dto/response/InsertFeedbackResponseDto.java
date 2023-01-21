package com.kustacks.kuring.feedback.common.dto.response;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.Getter;

@Getter
public class InsertFeedbackResponseDto extends ResponseDto {

    public InsertFeedbackResponseDto() {
        super(true, "성공", 201);
    }
}
