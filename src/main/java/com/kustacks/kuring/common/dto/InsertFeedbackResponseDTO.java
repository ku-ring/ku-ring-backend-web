package com.kustacks.kuring.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InsertFeedbackResponseDTO extends ResponseDTO {

    @Builder
    public InsertFeedbackResponseDTO() {
        super(true, "성공", 201);
    }
}
