package com.kustacks.kuring.user.common.dto;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.Getter;

@Getter
public class SaveFeedbackResponse extends ResponseDto {

    public SaveFeedbackResponse() {
        super(true, "성공", 201);
    }
}
