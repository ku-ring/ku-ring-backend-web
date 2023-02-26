package com.kustacks.kuring.feedback.common.dto.response;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.Getter;

@Getter
public class SaveFeedbackResponse extends ResponseDto {

    public SaveFeedbackResponse() {
        super(true, "성공", 201);
    }
}
